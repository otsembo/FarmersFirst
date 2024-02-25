package com.otsembo.farmersfirst.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.otsembo.farmersfirst.data.repository.AuthRepository
import com.otsembo.farmersfirst.data.repository.UserPreferencesRepository
import com.otsembo.farmersfirst.ui.navigation.AppRoutes
import com.otsembo.farmersfirst.ui.screens.auth.AuthScreen
import com.otsembo.farmersfirst.ui.screens.auth.AuthScreenVM
import com.otsembo.farmersfirst.ui.screens.auth.AuthUiState
import com.otsembo.farmersfirst.ui.screens.products.ProductsScreen
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    // inject view models
    private val authScreenVM: AuthScreenVM by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authRepo = AuthRepository(this, "", UserPreferencesRepository(this))


            val navController = rememberNavController()

            FarmersFirstTheme {
                // A surface container using the 'background' color from the theme

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
                                    isWideScreen = isWideScreen
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun HomePage(
    navController: NavHostController
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        paddingValues ->

        NavHost(navController = navController, startDestination = AppRoutes.Home.Products ){
            composable(AppRoutes.Home.Products){
                ProductsScreen(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues))
            }
        }

    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FarmersFirstTheme {
        Greeting("Android")
    }
}