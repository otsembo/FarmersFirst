package com.otsembo.farmersfirst.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.otsembo.farmersfirst.R
import com.otsembo.farmersfirst.ui.navigation.AppRoutes
import com.otsembo.farmersfirst.ui.screens.auth.AuthScreen
import com.otsembo.farmersfirst.ui.screens.auth.AuthScreenVM
import com.otsembo.farmersfirst.ui.screens.basket.BasketScreen
import com.otsembo.farmersfirst.ui.screens.basket.BasketScreenVM
import com.otsembo.farmersfirst.ui.screens.checkout.CheckoutScreen
import com.otsembo.farmersfirst.ui.screens.product_details.ProductDetailsScreen
import com.otsembo.farmersfirst.ui.screens.product_details.ProductDetailsScreenVM
import com.otsembo.farmersfirst.ui.screens.products.ProductsScreen
import com.otsembo.farmersfirst.ui.screens.products.ProductsScreenVM
import com.otsembo.farmersfirst.ui.screens.products.ProductsUiState
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme
import org.koin.android.ext.android.inject

/**
 * MainActivity is the main activity of the application.
 * It initializes the UI components and handles navigation.
 */
class MainActivity : ComponentActivity() {

    // inject view models
    private val authScreenVM: AuthScreenVM by inject()
    private val productsScreenVM: ProductsScreenVM by inject()
    private val productDetailsScreenVM: ProductDetailsScreenVM by inject()
    private val basketScreenVM: BasketScreenVM by inject()
    private val mainActivityVM: MainActivityVM by inject()

    /**
     * Initializes the MainActivity.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Collects the MainActivityUiState using mainActivityVM and updates accordingly
            val uiState: MainActivityUiState by mainActivityVM.mainActivityUiState.collectAsState()

            // Remembers the NavController state throughout the composition
            val navController = rememberNavController()

            // Remembers the state of whether the screen has been checked for being wide
            var hasCheckedWideScreen by rememberSaveable { mutableStateOf(false) }

            // Calculates the window size class and checks if the screen is wide
            val windowSize = calculateWindowSizeClass(activity = this)
            val isWideScreen = windowSize.widthSizeClass >= WindowWidthSizeClass.Medium

            // Executes the block if the screen width hasn't been checked yet
            if (!hasCheckedWideScreen) {
                LaunchedEffect(Unit) {
                    // Sets the wide screen state and updates hasCheckedWideScreen
                    mainActivityVM.handleActions(MainActivityActions.SetWideScreenState(isWideScreen))
                    hasCheckedWideScreen = true
                }
            }

            // Composes the UI with FarmersFirstTheme
            FarmersFirstTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Depending on the loading state, displays either loader or content
                    if (uiState.isLoading) {
                        MainActivityLoader(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        MainActivityContent(
                            uiState.isUserSignedIn,
                            navController,
                            isWideScreen,
                            authScreenVM,
                            productsScreenVM,
                            productDetailsScreenVM,
                            basketScreenVM
                        )
                    }
                }
            }

        }
    }

    /**
     * Composable function to display the main activity loader.
     *
     * @param modifier Modifier for the loader.
     */
    @Composable
    fun MainActivityLoader(
        modifier: Modifier = Modifier
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .zIndex(2f),
            shape = RectangleShape,
            color = Color(0x80000000),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(75.dp),
                    trackColor = MaterialTheme.colorScheme.onPrimary,
                    strokeCap = StrokeCap.Square
                )
                Text(
                    text = stringResource(id = R.string.welcome_quote),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    /**
     * Composable function to display the main activity content.
     *
     * @param isSignedIn Boolean indicating if the user is signed in.
     * @param navController NavController for navigation.
     * @param isWideScreen Boolean indicating if the screen is wide.
     * @param authScreenVM ViewModel for authentication screen.
     * @param productsScreenVM ViewModel for products screen.
     * @param productDetailsScreenVM ViewModel for product details screen.
     * @param basketScreenVM ViewModel for basket screen.
     */
    @Composable
    fun MainActivityContent(
        isSignedIn: Boolean = false,
        navController: NavHostController,
        isWideScreen: Boolean,
        authScreenVM: AuthScreenVM,
        productsScreenVM: ProductsScreenVM,
        productDetailsScreenVM: ProductDetailsScreenVM,
        basketScreenVM: BasketScreenVM
    ) {

        val scope = rememberCoroutineScope()

        // Navigates through different destinations based on the start destination and user actions
        NavHost(
            navController = navController,
            startDestination = if (isSignedIn) AppRoutes.AppHome else AppRoutes.AppAuth
        ) {

            // Authentication screen destination
            composable(AppRoutes.AppAuth) {
                AuthScreen(
                    isWideScreen = isWideScreen,
                    viewModel = authScreenVM,
                    navController = navController
                )
            }

            // Home navigation with sub-destinations
            navigation(startDestination = AppRoutes.Home.Products, route = AppRoutes.AppHome) {

                // Products screen destination
                composable(AppRoutes.Home.Products) {
                    ProductsScreen(
                        isWideScreen = isWideScreen,
                        viewModel = productsScreenVM,
                        navController = navController,
                    )
                }

                // Product details screen destination
                composable(AppRoutes.Home.ProductDetails) { backStackEntry ->
                    val productId = backStackEntry
                        .arguments
                        ?.getString(AppRoutes.Home.productId)
                        ?.toInt() ?: 0

                    ProductDetailsScreen(
                        isWideScreen = isWideScreen,
                        viewModel = productDetailsScreenVM,
                        navController = navController,
                        productId = productId,
                        scope = scope,
                    )
                }

                // Basket screen destination
                composable(AppRoutes.Home.Basket) {
                    BasketScreen(
                        isWideScreen = isWideScreen,
                        viewModel = basketScreenVM,
                        navController = navController
                    )
                }

                // User's basket screen destination
                composable(AppRoutes.Home.UserBasket) { backStackEntry ->
                    val userId = backStackEntry
                        .arguments
                        ?.getString(AppRoutes.Home.userId)
                        ?.toInt() ?: 0

                    BasketScreen(
                        isWideScreen = isWideScreen,
                        viewModel = basketScreenVM,
                        navController = navController,
                        userId = userId
                    )
                }

                // Checkout Screen
                composable(AppRoutes.Home.Checkout) {
                    CheckoutScreen(
                        navController
                    )
                }

            }
        }

    }
}
