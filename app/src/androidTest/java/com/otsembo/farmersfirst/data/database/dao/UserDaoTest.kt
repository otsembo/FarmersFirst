package com.otsembo.farmersfirst.data.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.otsembo.farmersfirst.data.database.DBTest
import com.otsembo.farmersfirst.data.database.testUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest : DBTest() {
    private lateinit var userDao: UserDao

    private suspend fun addUser() = userDao.create(testUser).first()

    @Before
    fun setUp() {
        initDB()
        userDao = UserDao(dbHelper.writableDatabase)
    }

    @Test
    fun testCreateUser_SuccessfullyAddsUserToDatabase() =
        runTest {
            val addedUser = addUser()
            addedUser?.let { user ->
                val results = userDao.find(user.id).first()
                assert(results != null) { "Could not add user to database" }
            }
        }

    @Test
    fun testDeleteUser_SuccessfullyDeletesUserFromDatabase() =
        runTest {
            val addedUser = addUser()
            addedUser?.let { user ->
                val deleted = userDao.delete(user.id).first()
                val results = userDao.find(user.id).first()
                assert(results == null && deleted) { "Could not remove user from database" }
            }
        }

    @Test
    fun testUpdateUser_SuccessfullyUpdatesUserInDatabase() =
        runTest {
            val addedUser = addUser()
            val updatedEmail = "updated@example.com"
            addedUser?.let { user ->
                val updated = userDao.update(user.copy(email = updatedEmail), user.id).first()
                val results = userDao.find(user.id).first()
                assert(updated != null && results != null && results.email == updatedEmail)
            }
        }

    @Test
    fun testFind_SuccessfullyRetrievesUserInDatabase() =
        runTest {
            val addedUser = addUser()
            addedUser?.let { user ->
                val results = userDao.find(user.id).first()
                assert(results != null) { "Could not find user in database" }
            }
        }

    @Test
    fun testFindAll_SuccessfullyRetrievesAllUsersInDatabase() =
        runTest {
            addUser()
            val results = userDao.findAll().last()
            assert(results.isNotEmpty()) { "Could not find all the users in the database" }
        }
}
