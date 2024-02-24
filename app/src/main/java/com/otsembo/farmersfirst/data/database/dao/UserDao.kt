package com.otsembo.farmersfirst.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Data access object (DAO) for interacting with the user table in the SQLite database.
 * This class implements the BaseDao<User> interface.
 * It provides methods for CRUD operations (create, read, update, delete) on user data.
 * @param db The SQLiteDatabase instance used for database operations.
 */
class UserDao(private val db: SQLiteDatabase) : BaseDao<User> {

    /**
     * Inserts a new user into the database.
     * @param item The user object to be inserted.
     * @return A Flow emitting the inserted user if successful, or null otherwise.
     */
    override suspend fun create(item: User): Flow<User?> =
        flow {
            val values = ContentValues().apply {
                put(AppDatabaseHelper.USER_EMAIL, item.email)
            }
            val insertId = db.insertOrThrow(AppDatabaseHelper.TABLE_USERS, null, values)
            if(insertId > 0) {
                item.id = insertId.toInt()
                emit(item)
            } else { emit(null) }
        }.catch { emit(null) }

    /**
     * Deletes a user from the database.
     * @param item The user object to be deleted.
     * @return A Flow emitting true if deletion is successful, or false otherwise.
     */
    override suspend fun delete(item: User): Flow<Boolean> =
        flow {
            db.delete(AppDatabaseHelper.TABLE_USERS, "id = ?", arrayOf(item.id.toString()))
            emit(true)
        }.catch { emit(false) }

    /**
     * Updates an existing user in the database.
     * @param item The updated user object.
     * @return A Flow emitting the updated user if successful, or null otherwise.
     */
    override suspend fun update(item: User): Flow<User?> =
        flow {
            db.delete(AppDatabaseHelper.TABLE_USERS, "id = ?", arrayOf(item.id.toString()))
            emitAll(find(item.id))
        }.catch { emit(null) }

    /**
     * Finds a user by their ID in the database.
     * @param id The ID of the user to find.
     * @return A Flow emitting the found user if successful, or null otherwise.
     */
    override suspend fun find(id: Int): Flow<User?> =
        flow {
            val userCursor = db.rawQuery("SELECT * FROM ${AppDatabaseHelper.TABLE_USERS} WHERE id = ? limit 1", arrayOf(id.toString()))
            if (userCursor.count < 1) {
                emit(null)
                userCursor.close()
            } else {
                while (userCursor.moveToNext()) {
                    emit(userCursor.getUser())
                    userCursor.close()
                }
            }
        }

    /**
     * Retrieves all users from the database.
     * @return A Flow emitting a list of all users in the database.
     */
    override suspend fun findAll(): Flow<List<User>> =
        flow {
            val users = mutableListOf<User>()
            val usersCursor = db.rawQuery("SELECT * FROM ${AppDatabaseHelper.TABLE_USERS}", null)
            while (usersCursor.moveToNext()) {
                users.add(usersCursor.getUser())
            }
            usersCursor.close()
            emit(users)
        }.catch { emit(mutableListOf()) }

    /**
     * Helper method to extract a User object from a Cursor.
     * @return The User object extracted from the Cursor.
     */
    private fun Cursor.getUser(): User {
        val id = getInt(0)
        val email = getString(1)
        return User(id, email)
    }
}
