package com.otsembo.farmersfirst

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.otsembo.farmersfirst.data.work.DbSeedWorker
import com.otsembo.farmersfirst.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class FarmersFirst: Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
        initDB()
    }

    private fun initDI(){
        startKoin {
            androidLogger()
            androidContext(this@FarmersFirst)
            modules(AppModule)
        }
    }

    // enqueue DB worker
    private fun initDB(){
        val initDBWorker = PeriodicWorkRequestBuilder<DbSeedWorker>(2, TimeUnit.DAYS)
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueueUniquePeriodicWork("dbInit", ExistingPeriodicWorkPolicy.KEEP, initDBWorker)
    }

}