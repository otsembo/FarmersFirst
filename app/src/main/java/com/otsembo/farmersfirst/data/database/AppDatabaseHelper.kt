package com.otsembo.farmersfirst.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Helper class for managing the SQLite database in the application.
 * @param context The context used for database operations.
 * @param factory An optional factory object to use for creating cursor objects, or null for the default behavior.
 * @param dbName The name of the database. Defaults to "farmers_first".
 */
class AppDatabaseHelper(
    context: Context,
    factory: SQLiteDatabase.CursorFactory? = null,
    dbName: String = DATABASE_NAME
) : SQLiteOpenHelper(context, dbName, factory, DATABASE_VERSION) {

    /**
     * Called when the database is created for the first time.
     * @param db The SQLiteDatabase instance representing the database.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.apply {
            // Execute SQL commands to create necessary tables
            execSQL(createUsersTableQuery)
            execSQL(createProductTableQuery)
            execSQL(createBasketTableQuery)
            execSQL(createBasketItemsTableQuery)
        }
    }

    /**
     * Called when the database needs to be upgraded.
     * @param db The SQLiteDatabase instance representing the database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop existing tables and recreate them
        refresh(db)
    }

    /**
     * Drops existing tables and recreates them.
     * @param db The SQLiteDatabase instance representing the database.
     */
    fun refresh(db: SQLiteDatabase?) {
        db?.apply {
            // Execute SQL commands to drop existing tables
            execSQL(dropBasketItemsTableQuery)
            execSQL(dropBasketTableQuery)
            execSQL(dropProductsTableQuery)
            execSQL(dropUsersTableQuery)
        }
        // Recreate tables
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "farmers_first"
        private const val DATABASE_VERSION = 1

        // Table names
        const val TABLE_PRODUCTS = "products"
        const val TABLE_USERS = "users"
        const val TABLE_BASKET = "basket"
        const val TABLE_BASKET_ITEMS = "basket_items"

        // Column names for products table
        const val PRODUCT_ID = "id"
        const val PRODUCT_NAME = "name"
        const val PRODUCT_DESC = "description"
        const val PRODUCT_STOCK = "items_stock"
        const val PRODUCT_PRICE = "unit_price"
        const val PRODUCT_IMAGE = "image_url"

        // Column names for users table
        const val USER_ID = "id"
        const val USER_EMAIL = "email_address"

        // Column names for basket table
        const val BASKET_ID = "id"
        const val BASKET_USER = "user_id"
        const val BASKET_STATUS = "status"
        val BasketStatusPending = "pending"
        val BasketStatusChecked = "checked"

        // Column names for basket items table
        const val BASKET_ITEM_ID = "id"
        const val BASKET_ITEM_BASKET = "basket_id"
        const val BASKET_ITEM_PRODUCT = "product_id"
        const val BASKET_ITEM_QTY = "quantity"

        // SQL queries to create tables
        private val createProductTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_PRODUCTS (
                $PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $PRODUCT_NAME TEXT NOT NULL,
                $PRODUCT_DESC TEXT NOT NULL,
                $PRODUCT_STOCK INTEGER NOT NULL,
                $PRODUCT_PRICE REAL NOT NULL,
                $PRODUCT_IMAGE TEXT NOT NULL
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

        // SQL queries to drop tables
        private const val dropProductsTableQuery = "DROP TABLE IF EXISTS $TABLE_PRODUCTS;"
        private const val dropUsersTableQuery = "DROP TABLE IF EXISTS $TABLE_USERS;"
        private const val dropBasketTableQuery = "DROP TABLE IF EXISTS $TABLE_BASKET;"
        private const val dropBasketItemsTableQuery = "DROP TABLE IF EXISTS $TABLE_BASKET_ITEMS;"
    }
}
