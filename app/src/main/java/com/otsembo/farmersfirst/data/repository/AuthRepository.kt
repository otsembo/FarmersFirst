package com.otsembo.farmersfirst.data.repository

 import android.content.Context
 import androidx.credentials.CredentialManager
 import androidx.credentials.GetCredentialRequest
 import androidx.credentials.exceptions.NoCredentialException
 import com.google.android.libraries.identity.googleid.GetGoogleIdOption
 import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
 import com.otsembo.farmersfirst.common.AppResource
 import com.otsembo.farmersfirst.common.coerceTo
 import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
 import com.otsembo.farmersfirst.data.database.dao.UserDao
 import com.otsembo.farmersfirst.data.model.Basket
 import com.otsembo.farmersfirst.data.model.User
 import kotlinx.coroutines.flow.Flow
 import kotlinx.coroutines.flow.catch
 import kotlinx.coroutines.flow.emitAll
 import kotlinx.coroutines.flow.flow
 import kotlinx.coroutines.flow.last
 import kotlinx.coroutines.flow.map
 import java.security.MessageDigest
 import java.util.UUID

/**
 * Interface for the authentication repository, defining methods for signing in and signing out users.
 */
interface IAuthRepository {

    /**
     * Signs in the user with the specified Google ID option.
     * @param googleIdOption The optional Google ID option for signing in. Defaults to null.
     * @return A flow of AppResource representing the result of the sign-in operation.
     */
    suspend fun signInUser(googleIdOption: GetGoogleIdOption? = null): Flow<AppResource<String?>>

    /**
     * Signs out the currently signed-in user.
     * @return A flow of AppResource representing the result of the sign-out operation.
     */
    suspend fun signOutUser(): Flow<AppResource<Boolean>>

    suspend fun checkIfSignedIn(): Flow<AppResource<Boolean>>
}


/**
 * Repository class responsible for handling authentication-related operations,
 * such as signing in and signing out users using Google OAuth.
 * Implements the [IAuthRepository] interface.
 *
 * @param activityContext The context of the activity or application.
 * @param oAuthClient The OAuth client identifier.
 * @param userPrefRepository The repository for user preferences.
 */
class AuthRepository (
    private val activityContext: Context,
    private val oAuthClient: String,
    private val userPrefRepository: IUserPrefRepository,
    private val basketRepository: IBasketRepository,

): IAuthRepository{

    private val credentialManager = CredentialManager.create(activityContext)
    private val userDao: UserDao = UserDao(AppDatabaseHelper(activityContext).writableDatabase)

    /**
     * Signs in the user with the specified Google ID option.
     * If no option is provided, a default option is constructed.
     * Emits [AppResource] objects representing the result of the sign-in operation.
     *
     * @param googleIdOption The optional Google ID option for signing in. Defaults to null.
     * @return A flow of [AppResource] representing the result of the sign-in operation.
     */
    override suspend fun signInUser(googleIdOption: GetGoogleIdOption?): Flow<AppResource<String?>> =
        flow {

            emit(AppResource.Loading())

            val googleId: GetGoogleIdOption = googleIdOption ?: buildGoogleId(true)

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleId)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            val tokenCredential = GoogleIdTokenCredential
                .createFrom(result.credential.data)

            val userEmail = tokenCredential.data.getString(EMAIL_KEY)
            val signInToken = tokenCredential.idToken

            userEmail?.let {
                val user = userDao.queryWhere("${AppDatabaseHelper.USER_EMAIL} = ?", arrayOf(it)).last()
                val userId: Int = if(user.isEmpty()){
                    // if no existing user, create one then throw exception if error occurs
                    val createdUser = userDao.create(User(id = 0, it)).last() ?: throw Exception("Could not create your account!")
                    // create their initial basket
                    basketRepository.createBasket(
                        Basket(
                            id = 0,
                            user = User(id = createdUser.id, email =  ""),
                            status = AppDatabaseHelper.BasketStatusPending
                        )
                    ).last()
                    createdUser.id
                } else user.first().id

                emitAll(userPrefRepository.addUserToStore(signInToken, userId).map { tokenStoreResult ->
                    tokenStoreResult.coerceTo { res ->
                        when(res){
                            is AppResource.Success -> signInToken
                            else-> null
                        }
                    }
                })

            }
        }.catch { cause: Throwable ->
            if(cause is NoCredentialException)
                signInUser(googleIdOption = buildGoogleId(false))
            else
                emit(AppResource.Error(info = cause.message ?: "An unexpected error occurred"))
        }

    /**
     * Signs out the currently signed-in user.
     * Emits [AppResource] objects representing the result of the sign-out operation.
     *
     * @return A flow of [AppResource] representing the result of the sign-out operation.
     */
    override suspend fun signOutUser(): Flow<AppResource<Boolean>> =
        flow {
            emit(AppResource.Loading())
            val logout = userPrefRepository.removeUserFromStore().last()
            if (logout.data == true) emit(AppResource.Success(true))
            else emit(AppResource.Error("Something went wrong"))
        }.catch { emit(AppResource.Error(it.message ?: "Something went wrong")) }

    override suspend fun checkIfSignedIn(): Flow<AppResource<Boolean>> =
        flow {
            emit(AppResource.Loading())
            emitAll(userPrefRepository.fetchToken().map { res ->
                res.coerceTo {
                    when(it){
                        is AppResource.Error -> false
                        is AppResource.Loading -> false
                        is AppResource.Success -> true
                    }
                }
            })
        }

    /**
     * Builds a Google ID option for signing in based on the provided authorized filter.
     *
     * @param authorizedFilter Boolean indicating whether to filter by authorized accounts.
     * @return A constructed [GetGoogleIdOption] object for signing in.
     */
    private fun buildGoogleId(authorizedFilter: Boolean): GetGoogleIdOption =
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(authorizedFilter)
            .setNonce(buildNonce())
            .setServerClientId(KEY_HERE)
            .build()

    /**
     * Generates a unique nonce string for authentication purposes.
     *
     * @return The generated nonce string.
     */
    private fun buildNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val mDigest = MessageDigest.getInstance("SHA-512")
        val digest = mDigest.digest(bytes)
        return digest.fold("") { hash, it -> hash + "%02x".format(it) }
    }

    companion object {
        private const val EMAIL_KEY = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID"
        private const val SERVER_CLIENT_KEY = "oauth_client"
        private const val KEY_HERE = "947093319510-ucujl9r8i6lu33abri6eue7rlp5vgsm3.apps.googleusercontent.com"
    }

}
