package com.otsembo.farmersfirst.ui.screens.productDetails

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.otsembo.farmersfirst.R
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.ui.components.AppBar
import com.otsembo.farmersfirst.ui.components.AppBarIcon
import com.otsembo.farmersfirst.ui.components.EmptyEntityMessage
import com.otsembo.farmersfirst.ui.components.ErrorScreen
import com.otsembo.farmersfirst.ui.components.LoadingScreen
import com.otsembo.farmersfirst.ui.navigation.AppRoutes
import com.otsembo.farmersfirst.ui.theme.image_tint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Composable function for displaying the product details screen.
 *
 * @param modifier Modifier to be applied to the root element.
 * @param isWideScreen Boolean indicating if the screen is in wide mode.
 * @param navController NavHostController for navigation within the app.
 * @param viewModel ProductDetailsScreenVM providing the view model for this screen.
 * @param productId The ID of the product to display details for.
 * @param scope CoroutineScope for managing coroutines in this composable.
 */
@Composable
fun ProductDetailsScreen(
    modifier: Modifier = Modifier,
    isWideScreen: Boolean = false,
    navController: NavHostController,
    viewModel: ProductDetailsScreenVM,
    productId: Int,
    scope: CoroutineScope,
) {
    val uiState: ProductDetailsUiState by viewModel.productDetailsUiState.collectAsState()
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }

    LaunchedEffect(key1 = uiState.isLoading) {
        viewModel.handleActions(ProductDetailsActions.LoadProduct(productId))
    }

    when {
        uiState.isLoading -> LoadingScreen()
        uiState.errorOccurred -> ErrorScreen(errorMessage = uiState.errorMessage)
        uiState.product == null -> EmptyEntityMessage(message = "Could not find product")
        else -> {
            // fetch product
            val product = uiState.product!!

            if (isWideScreen) {
                Box(
                    modifier =
                        modifier
                            .fillMaxSize(),
                ) {
                    ProductDetailsAppBar(
                        navController = navController,
                        modifier =
                            Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.TopStart),
                    )

                    // Background image
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.description,
                        modifier =
                            Modifier
                                .fillMaxWidth(0.75f)
                                .fillMaxHeight(),
                        contentScale = ContentScale.Crop,
                        colorFilter =
                            ColorFilter.tint(
                                image_tint,
                                blendMode = BlendMode.SrcAtop,
                            ),
                    )

                    ElevatedCard(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        shape =
                            RoundedCornerShape(
                                topStart = dimensionResource(id = R.dimen.banner_card_radius),
                            ),
                    ) {
                        ProductDetailsContent(
                            product = product,
                            cartCount = uiState.cartCount,
                            onAddToCart = { x, y ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Adding ${product.name} to your basket",
                                    )
                                }
                                viewModel.handleActions(ProductDetailsActions.AddToCart(x, y))
                            },
                            updateCount = { currentCount, increase ->
                                val cartCount = if (increase) currentCount + 1 else currentCount - 1
                                viewModel.handleActions(
                                    ProductDetailsActions.CartCountChange(cartCount),
                                )
                            },
                        )
                    }
                }
            } else {
                Box(
                    modifier = modifier.fillMaxSize(),
                ) {
                    ProductDetailsAppBar(
                        navController = navController,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart),
                    )

                    // Background image
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.description,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f),
                        contentScale = ContentScale.Crop,
                        colorFilter =
                            ColorFilter.tint(
                                image_tint,
                                blendMode = BlendMode.SrcAtop,
                            ),
                    )

                    ElevatedCard(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        shape =
                            RoundedCornerShape(
                                topStart = dimensionResource(id = R.dimen.banner_card_radius),
                                topEnd = dimensionResource(id = R.dimen.banner_card_radius),
                            ),
                    ) {
                        ProductDetailsContent(
                            product = product,
                            cartCount = uiState.cartCount,
                            onAddToCart = { x, y ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Adding ${product.name} to your basket",
                                    )
                                }
                                viewModel.handleActions(ProductDetailsActions.AddToCart(x, y))
                            },
                            updateCount = { currentCount, increase ->
                                val cartCount = if (increase) currentCount + 1 else currentCount - 1
                                viewModel.handleActions(
                                    ProductDetailsActions.CartCountChange(cartCount),
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = ButtonDefaults.MinHeight * 2),
    ) {
        SnackbarHost(
            snackbarHostState,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter),
        )
    }
}

/**
 * Composable function for displaying the content of the product details screen.
 *
 * @param product The product to display details for.
 * @param cartCount The current count of items in the cart.
 * @param updateCount Callback function to update the count of items in the cart.
 * @param onAddToCart Callback function to add the product to the cart.
 */
@Composable
fun ProductDetailsContent(
    product: Product,
    cartCount: Int,
    updateCount: (Int, Boolean) -> Unit,
    onAddToCart: (Int, Int) -> Unit,
) {
    val inStock = product.stock > 0

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = .5.sp,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$${product.price}",
                modifier =
                    Modifier
                        .padding(top = 1.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            CartCounter(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd),
                actionModifier =
                    Modifier
                        .width(50.dp),
                inputModifier =
                    Modifier
                        .width(60.dp),
                updateCount = updateCount,
                cartCount = cartCount,
                productStock = product.stock,
            )
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        Text(
            text = product.description,
            modifier =
                Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = ButtonDefaults.MinHeight + 16.dp,
                    )
                    .scrollable(
                        rememberScrollState(),
                        orientation = Orientation.Vertical,
                    ),
            overflow = TextOverflow.Clip,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = { onAddToCart(0, product.id) },
            shape = RoundedCornerShape(10.dp),
            enabled = inStock,
        ) {
            Text(
                text = if (inStock) "Add to basket" else "Out of stock",
                style = MaterialTheme.typography.bodyMedium,
                modifier =
                    Modifier
                        .padding(8.dp),
            )
        }
    }
}

/**
 * Composable function for displaying the app bar of the product details screen.
 *
 * @param modifier Modifier to be applied to the root element.
 * @param navController NavHostController for navigation within the app.
 */
@Composable
fun ProductDetailsAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    AppBar(
        modifier =
            modifier
                .zIndex(1f)
                .padding(top = 16.dp),
        startIcon = {
            AppBarIcon(
                icon = Icons.AutoMirrored.Default.ArrowBack,
                onClick = { navController.popBackStack() },
                tint = MaterialTheme.colorScheme.surface,
            )
        },
        endIcon = {
            AppBarIcon(
                icon = Icons.Default.ShoppingCart,
                onClick = { navController.navigate(AppRoutes.Home.Basket) },
                tint = MaterialTheme.colorScheme.surface,
            )
        },
    )
}

@Composable
fun CartCounter(
    modifier: Modifier = Modifier,
    actionModifier: Modifier = Modifier,
    inputModifier: Modifier = Modifier,
    inputShape: Shape = RoundedCornerShape(10.dp),
    cartCount: Int,
    productStock: Int,
    updateCount: (Int, Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FloatingActionButton(
            modifier = actionModifier,
            onClick = { if (cartCount > 1) updateCount(cartCount, false) },
            containerColor = MaterialTheme.colorScheme.tertiary,
            shape = inputShape,
        ) {
            Text(
                text = "-",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        Text(
            modifier = inputModifier,
            text = "$cartCount",
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )

        FloatingActionButton(
            modifier = actionModifier,
            onClick = { if (productStock > cartCount) updateCount(cartCount, true) },
            containerColor = MaterialTheme.colorScheme.tertiary,
            shape = inputShape,
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}
