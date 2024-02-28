package com.otsembo.farmersfirst.data.work

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.database.FarmersDBSeed
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker class for seeding the database with initial data.
 * @param context The application context.
 * @param workerParams The parameters for the worker.
 */
class DbSeedWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    /**
     * Performs the work of seeding the database with initial data.
     * @return The result of the work.
     */
    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val appDB = AppDatabaseHelper(context)
                val productDao = ProductDao(appDB.writableDatabase)

                val dbSeeder = FarmersDBSeed(appDB, productDao)
                dbSeeder.addProducts()

                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
}