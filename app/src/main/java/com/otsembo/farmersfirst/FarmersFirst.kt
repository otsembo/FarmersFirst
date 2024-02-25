package com.otsembo.farmersfirst

import android.app.Application
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.database.IFarmersDBSeed
import com.otsembo.farmersfirst.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FarmersFirst: Application() {

    private lateinit var dbSeed: IFarmersDBSeed
    override fun onCreate() {
        super.onCreate()
        initDI()
        val seed: IFarmersDBSeed by inject()
        dbSeed = seed
        initDB()
    }

    private fun initDI(){
        startKoin {
            androidLogger()
            androidContext(this@FarmersFirst)
            modules(AppModule)
        }
    }

    private fun initDB(){
        CoroutineScope(Dispatchers.IO).launch {
            dbSeed.addProducts()
        }
    }

}