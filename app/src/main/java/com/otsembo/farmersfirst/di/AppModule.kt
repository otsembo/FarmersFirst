package com.otsembo.farmersfirst.di

import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.database.FarmersDBSeed
import com.otsembo.farmersfirst.data.database.IFarmersDBSeed
import com.otsembo.farmersfirst.data.database.dao.BasketDao
import com.otsembo.farmersfirst.data.database.dao.BasketItemDao
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import com.otsembo.farmersfirst.data.database.dao.UserDao
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.repository.AuthRepository
import com.otsembo.farmersfirst.data.repository.BaseRecommenderRepository
import com.otsembo.farmersfirst.data.repository.BasketItemsRecommenderRepository
import com.otsembo.farmersfirst.data.repository.BasketRepository
import com.otsembo.farmersfirst.data.repository.IAuthRepository
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IProductRepository
import com.otsembo.farmersfirst.data.repository.IUserPrefRepository
import com.otsembo.farmersfirst.data.repository.ProductRepository
import com.otsembo.farmersfirst.data.repository.UserPreferencesRepository
import com.otsembo.farmersfirst.ui.MainActivityVM
import com.otsembo.farmersfirst.ui.screens.auth.AuthScreenVM
import com.otsembo.farmersfirst.ui.screens.basket.BasketScreenVM
import com.otsembo.farmersfirst.ui.screens.product_details.ProductDetailsScreenVM
import com.otsembo.farmersfirst.ui.screens.products.ProductsScreenVM
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

/**
 * This module provides dependencies for the application, including database setup, DAOs, and repositories.
 * It defines single instances for the database and its DAOs, as well as repositories for user preferences,
 * authentication, basket management, and product management.
 */

val AppModule = module {
    // database setup
    single <AppDatabaseHelper> { AppDatabaseHelper(context = androidContext()) }
    single <SQLiteDatabase> { get<AppDatabaseHelper>().writableDatabase  }

    // DAO setup
    single <UserDao> { UserDao(get()) }
    single <ProductDao> { ProductDao(get()) }
    single <BasketDao> { BasketDao(get(), get()) }
    single <BasketItemDao> { BasketItemDao(get(), get(), get()) }

    // Repositories
    single <IUserPrefRepository> { UserPreferencesRepository(context = androidContext()) }
    single <IAuthRepository> { AuthRepository(activityContext = androidContext(), "", get(), get()) }
    single <IBasketRepository> { BasketRepository(get(), get(), get()) }
    single <IProductRepository> { ProductRepository(get()) }
    single <IFarmersDBSeed> { FarmersDBSeed(get(), get()) }
    factory <BasketItemsRecommenderRepository> { BasketItemsRecommenderRepository(get(), get(), get()) }

    // ViewModels
    viewModel <AuthScreenVM> { AuthScreenVM(androidApplication(), get()) }
    viewModel <ProductsScreenVM> { ProductsScreenVM(get(), get(), get(), get()) }
    viewModel <ProductDetailsScreenVM> { ProductDetailsScreenVM(get(), get(), get()) }
    viewModel <BasketScreenVM>{ BasketScreenVM(get(), get()) }
    viewModel <MainActivityVM> { MainActivityVM(get()) }

}