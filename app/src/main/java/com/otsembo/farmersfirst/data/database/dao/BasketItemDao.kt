package com.otsembo.farmersfirst.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.BasketItem
import kotlinx.coroutines.flow.last

/**
 * Data access object (DAO) for performing CRUD operations on the basket_item table in the SQLite database.
 * This class implements the BaseDao<BasketItem> interface.
 * It provides methods for creating, reading, updating, and deleting basket item data.
 * @param db The SQLiteDatabase instance used for database operations.
 * @param basketDao The BasketDao instance used for retrieving basket data associated with basket items.
 * @param productDao The ProductDao instance used for retrieving product data associated with basket items.
 */
class BasketItemDao(
    db: SQLiteDatabase,
    private val basketDao: BasketDao,
    private val productDao: ProductDao,
) : BaseDao<BasketItem>(db, AppDatabaseHelper.TABLE_BASKET_ITEMS) {
    /**
     * Constructs a BasketItem object from the current cursor position.
     */
    override suspend fun Cursor.buildEntity(): BasketItem {
        val basket = basketDao.find(id = getInt(1)).last()!!
        val product = productDao.find(id = getInt(2)).last()!!
        return BasketItem(
            id = getInt(0),
            basket = basket,
            product = product,
            quantity = getInt(3),
        )
    }

    /**
     * Retrieves content values from the given BasketItem object.
     */
    override fun getContentValues(item: BasketItem): ContentValues {
        return ContentValues().apply {
            put(AppDatabaseHelper.BASKET_ITEM_BASKET, item.basket.id)
            put(AppDatabaseHelper.BASKET_ITEM_PRODUCT, item.product.id)
            put(AppDatabaseHelper.BASKET_ITEM_QTY, item.quantity)
        }
    }

    /**
     * Sets the ID of the BasketItem object.
     */
    override fun setEntityId(
        item: BasketItem,
        id: Int,
    ) {
        item.id = id
    }
}
