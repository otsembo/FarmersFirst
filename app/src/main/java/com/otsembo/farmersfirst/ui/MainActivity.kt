package com.otsembo.farmersfirst.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.otsembo.farmersfirst.data.repository.AuthRepository
import com.otsembo.farmersfirst.data.repository.UserPreferencesRepository
import com.otsembo.farmersfirst.ui.navigation.AppRoutes
import com.otsembo.farmersfirst.ui.screens.auth.AuthScreen
import com.otsembo.farmersfirst.ui.screens.auth.AuthScreenVM
import com.otsembo.farmersfirst.ui.screens.basket.BasketScreen
import com.otsembo.farmersfirst.ui.screens.basket.BasketScreenVM
import com.otsembo.farmersfirst.ui.screens.product_details.ProductDetailsScreen
import com.otsembo.farmersfirst.ui.screens.product_details.ProductDetailsScreenVM
import com.otsembo.farmersfirst.ui.screens.products.ProductsScreen
import com.otsembo.farmersfirst.ui.screens.products.ProductsScreenVM
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    // inject view models
    private val authScreenVM: AuthScreenVM by inject()
    private val productsScreenVM: ProductsScreenVM by inject()
    private val productDetailsScreenVM: ProductDetailsScreenVM by inject()
    private val basketScreenVM: BasketScreenVM by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            FarmersFirstTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val windowSize = calculateWindowSizeClass(activity = this)
                    val isWideScreen = windowSize.widthSizeClass >= WindowWidthSizeClass.Medium

                    NavHost(navController = navController, startDestination = AppRoutes.AppAuth ){

                        composable(AppRoutes.AppAuth){
                            AuthScreen(
                                isWideScreen = isWideScreen,
                                viewModel = authScreenVM,
                                navController = navController
                            )
                        }

                        navigation(startDestination = AppRoutes.Home.Products, route = AppRoutes.AppHome){

                            composable(AppRoutes.Home.Products){
                                ProductsScreen(
                                    isWideScreen = isWideScreen,
                                    viewModel = productsScreenVM,
                                    navController = navController
                                )
                            }

                            composable(AppRoutes.Home.ProductDetails){  backStackEntry ->

                                val productId = backStackEntry
                                    .arguments
                                    ?.getString(AppRoutes.Home.productId)
                                    ?.toInt() ?: 0

                                ProductDetailsScreen(
                                    isWideScreen = isWideScreen,
                                    viewModel = productDetailsScreenVM,
                                    navController = navController,
                                    productId = productId
                                )
                            }

                            composable(AppRoutes.Home.Basket){
                                BasketScreen(
                                    isWideScreen = isWideScreen,
                                    viewModel = basketScreenVM,
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

