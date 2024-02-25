package com.otsembo.farmersfirst

import android.app.Application
import com.otsembo.farmersfirst.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FarmersFirst: Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI(){
        startKoin {
            androidLogger()
            androidContext(this@FarmersFirst)
            modules(AppModule)
        }
    }

}