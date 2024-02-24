package com.otsembo.farmersfirst.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.Basket
import kotlinx.coroutines.flow.last

/**
 * Data access object (DAO) for performing CRUD operations on the basket table in the SQLite database.
 * This class implements the BaseDao<Basket> interface.
 * It provides methods for creating, reading, updating, and deleting basket data.
 * @param db The SQLiteDatabase instance used for database operations.
 * @param userDao The UserDao instance used for retrieving user data associated with baskets.
 */
class BasketDao(db: SQLiteDatabase, private val userDao: UserDao) :
    BaseDao<Basket>(db, AppDatabaseHelper.TABLE_BASKET) {

    /**
     * Constructs a Basket object from the current cursor position.
     */
    override suspend fun Cursor.buildEntity(): Basket {
        val user = userDao.find(id = getInt(1)).last()
        return Basket(
            id = getInt(0),
            user = user!!,
            status = getString(2)
        )
    }

    /**
     * Retrieves content values from the given Basket object.
     */
    override fun getContentValues(item: Basket): ContentValues =
        ContentValues().apply {
            put(AppDatabaseHelper.BASKET_USER, item.user.id)
            put(AppDatabaseHelper.BASKET_STATUS, item.status)
        }

    /**
     * Sets the ID of the Basket object.
     */
    override fun setEntityId(item: Basket, id: Int) {
        item.id = id
    }
}
