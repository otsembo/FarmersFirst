package com.otsembo.farmersfirst.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.Product

/**
 * Data access object (DAO) for performing CRUD operations on the product table in the SQLite database.
 * @param db The SQLiteDatabase instance used for database operations.
 */
class ProductDao(db: SQLiteDatabase) :
    BaseDao<Product>(db, AppDatabaseHelper.TABLE_PRODUCTS) {

    /**
     * Builds a Product entity from the current cursor position.
     */
    override suspend fun Cursor.buildEntity(): Product =
        Product(
            id = getInt(0),
            name = getString(1),
            description = getString(2),
            stock = getInt(3),
            price = getFloat(4),
            image = getString(5)
        )

    /**
     * Retrieves content values from the given Product entity.
     */
    override fun getContentValues(item: Product): ContentValues =
        ContentValues().apply {
            put(AppDatabaseHelper.PRODUCT_STOCK, item.stock)
            put(AppDatabaseHelper.PRODUCT_PRICE, item.price)
            put(AppDatabaseHelper.PRODUCT_IMAGE, item.image)
            put(AppDatabaseHelper.PRODUCT_DESC, item.description)
            put(AppDatabaseHelper.PRODUCT_NAME, item.name)
        }

    /**
     * Sets the ID of the Product entity.
     */
    override fun setEntityId(item: Product, id: Int) {
        item.id = id
    }
}
