package com.otsembo.farmersfirst

import android.app.Application
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FarmersFirst: Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
//        initDB()
    }

    private fun initDI(){
        startKoin {
            androidLogger()
            androidContext(this@FarmersFirst)
            modules(AppModule)
        }
    }

    private fun initDB(){
        AppDatabaseHelper(this).writableDatabase
    }

}