package com.otsembo.farmersfirst.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.Basket
import kotlinx.coroutines.flow.last

class BasketDao(db: SQLiteDatabase, private val userDao: UserDao):
    BaseDao<Basket>(db, AppDatabaseHelper.TABLE_BASKET){
    override suspend fun Cursor.buildEntity(): Basket {
        val user = userDao.find(id = getInt(1)).last()
        return Basket(
            id = getInt(0),
            user = user!!,
            status = getString(2)
        )
    }

    override fun getContentValues(item: Basket): ContentValues =
        ContentValues().apply {
            put(AppDatabaseHelper.BASKET_USER, item.user.id)
            put(AppDatabaseHelper.BASKET_STATUS, item.status)
        }

    override fun setEntityId(item: Basket, id: Int) {
        item.id = id
    }
}