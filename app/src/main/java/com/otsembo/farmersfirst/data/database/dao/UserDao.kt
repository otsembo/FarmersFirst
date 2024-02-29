package com.otsembo.farmersfirst.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.User

/**
 * Data access object (DAO) for performing CRUD operations on the user table in the SQLite database.
 * This class implements the BaseDao<User> interface.
 * It provides methods for creating, reading, updating, and deleting user data.
 * @param db The SQLiteDatabase instance used for database operations.
 */
class UserDao(db: SQLiteDatabase) :
    BaseDao<User>(db, AppDatabaseHelper.TABLE_USERS) {
    /**
     * Constructs a User object from the current cursor position.
     */
    override suspend fun Cursor.buildEntity(): User {
        val id = getInt(0)
        val email = getString(1)
        return User(id, email)
    }

    /**
     * Sets the ID of the User object.
     */
    override fun setEntityId(
        item: User,
        id: Int,
    ) {
        item.id = id
    }

    /**
     * Retrieves content values from the given User object.
     */
    override fun getContentValues(item: User): ContentValues =
        ContentValues().apply {
            put(AppDatabaseHelper.USER_EMAIL, item.email)
        }
}
