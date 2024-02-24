package com.otsembo.farmersfirst.data.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.model.User
import org.junit.After

val testUser = User(1, "email@mail.com")
val testProduct = Product(1, "fertilizer", "long fertilizer", 20, 12.5f, "")

abstract class DBTest {

    lateinit var dbHelper: AppDatabaseHelper
    fun initDB(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = AppDatabaseHelper(context, null, "farmers_first_test")
    }

    @After
    fun tearDown() {
        dbHelper.refresh(dbHelper.writableDatabase)
        dbHelper.close()
    }
}
