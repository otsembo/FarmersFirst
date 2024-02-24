package com.otsembo.farmersfirst.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AppDatabaseHelper(
    context: Context,
    factory: SQLiteDatabase.CursorFactory? = null,
    dbName: String = DATABASE_NAME
) : SQLiteOpenHelper(context, dbName, factory, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        db?.apply {
            execSQL(createUsersTableQuery)
            execSQL(createProductTableQuery)
            execSQL(createBasketTableQuery)
            execSQL(createBasketItemsTableQuery)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        refresh(db)
    }

    fun refresh(db: SQLiteDatabase?){
        db?.apply {
            execSQL(dropBasketItemsTableQuery)
            execSQL(dropBasketTableQuery)
            execSQL(dropProductsTableQuery)
            execSQL(dropUsersTableQuery)
        }
        onCreate(db)
    }


    companion object {
        private const val DATABASE_NAME = "farmers_first"
        private const val DATABASE_VERSION = 1

        const val TABLE_PRODUCTS = "products"
        const val PRODUCT_ID = "id"
        const val PRODUCT_NAME = "name"
        const val PRODUCT_STOCK = "items_stock"
        const val PRODUCT_PRICE = "unit_price"

        const val TABLE_USERS = "users"
        const val USER_ID = "id"
        const val USER_EMAIL = "email_address"

        const val TABLE_BASKET = "basket"
        const val BASKET_ID = "id"
        const val BASKET_USER = "user_id"
        const val BASKET_STATUS = "status"

        const val TABLE_BASKET_ITEMS = "basket_items"
        const val BASKET_ITEM_ID = "id"
        const val BASKET_ITEM_BASKET = "basket_id"
        const val BASKET_ITEM_PRODUCT = "product_id"
        const val BASKET_ITEM_QTY = "quantity"

        private val createProductTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_PRODUCTS (
                $PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $PRODUCT_NAME TEXT NOT NULL,
                $PRODUCT_STOCK INTEGER NOT NULL,
                $PRODUCT_PRICE REAL NOT NULL
            )
        """.trimIndent()

        private val createUsersTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                $USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $USER_EMAIL TEXT NOT NULL UNIQUE
            )
        """.trimIndent()

        private val createBasketTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_BASKET (
                $BASKET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $BASKET_USER INTEGER NOT NULL,
                $BASKET_STATUS TEXT NOT NULL,
                FOREIGN KEY ($BASKET_USER) REFERENCES $TABLE_USERS($USER_ID)
            )
        """.trimIndent()

        private val createBasketItemsTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_BASKET_ITEMS (
                $BASKET_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $BASKET_ITEM_BASKET INTEGER NOT NULL,
                $BASKET_ITEM_PRODUCT INTEGER NOT NULL,
                $BASKET_ITEM_QTY INTEGER NOT NULL,
                FOREIGN KEY ($BASKET_ITEM_BASKET) REFERENCES $TABLE_BASKET($BASKET_ID),
                FOREIGN KEY ($BASKET_ITEM_PRODUCT) REFERENCES $TABLE_PRODUCTS($PRODUCT_ID)
            )
        """.trimIndent()

        private val dropProductsTableQuery = "DROP TABLE IF EXISTS $TABLE_PRODUCTS;"
        private val dropUsersTableQuery = "DROP TABLE IF EXISTS $TABLE_USERS;"
        private val dropBasketTableQuery = "DROP TABLE IF EXISTS $TABLE_BASKET;"
        private val dropBasketItemsTableQuery = "DROP TABLE IF EXISTS $TABLE_BASKET_ITEMS;"


    }

}