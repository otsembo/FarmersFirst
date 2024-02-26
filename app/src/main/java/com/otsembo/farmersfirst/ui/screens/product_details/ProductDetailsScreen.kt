package com.otsembo.farmersfirst.ui.screens.product_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.otsembo.farmersfirst.R
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.ui.components.AppBar
import com.otsembo.farmersfirst.ui.components.AppBarIcon
import com.otsembo.farmersfirst.ui.components.EmptyEntityMessage
import com.otsembo.farmersfirst.ui.components.ErrorScreen
import com.otsembo.farmersfirst.ui.components.LoadingScreen
import com.otsembo.farmersfirst.ui.navigation.AppRoutes
import com.otsembo.farmersfirst.ui.screens.auth.AuthActions
import com.otsembo.farmersfirst.ui.screens.auth.AuthCardUi
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme
import com.otsembo.farmersfirst.ui.theme.image_tint

@Composable
fun ProductDetailsScreen(
    modifier: Modifier = Modifier,
    isWideScreen: Boolean = false,
    navController: NavHostController,
    viewModel: ProductDetailsScreenVM,
    productId: Int,
) {
    val uiState: ProductDetailsUiState by viewModel.productDetailsUiState.collectAsState()

    LaunchedEffect(key1 = uiState.isLoading){
        viewModel.handleActions(ProductDetailsActions.LoadProduct(productId))
    }

    when {
        uiState.isLoading -> LoadingScreen()
        uiState.errorOccurred -> ErrorScreen(errorMessage = uiState.errorMessage)
        uiState.product == null -> EmptyEntityMessage(message = "Could not find product")
        else -> {
            // fetch product
            val product = uiState.product!!

            if(isWideScreen){
                Box(modifier = modifier
                    .fillMaxSize()){

                    ProductDetailsAppBar(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .align(Alignment.TopStart))

                    // Background image
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.description,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(image_tint, blendMode = BlendMode.SrcAtop)
                    )

                    ElevatedCard(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.banner_card_radius),
                        ),
                    ) {
                        ProductDetailsContent(
                            product = product,
                            cartCount = uiState.cartCount,
                            updateCount = { currentCount, increase ->
                                val cartCount = if(increase) currentCount + 1 else currentCount - 1
                                viewModel.handleActions(ProductDetailsActions.CartCountChange(cartCount))
                            })
                    }

                }

            }else{

                Box(
                    modifier = modifier.fillMaxSize()
                ) {

                    ProductDetailsAppBar(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart))

                    // Background image
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(image_tint, blendMode = BlendMode.SrcAtop)
                    )

                    ElevatedCard(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.banner_card_radius),
                            topEnd = dimensionResource(id = R.dimen.banner_card_radius)
                        ),
                    ) {

                        ProductDetailsContent(
                            product = product,
                            cartCount = uiState.cartCount,
                            updateCount = { currentCount, increase ->
                                val cartCount = if(increase) currentCount + 1 else currentCount - 1
                                viewModel.handleActions(ProductDetailsActions.CartCountChange(cartCount))
                            })

                    }
                }
            }
        }
    }


}



@Composable
fun ProductDetailsContent(
    product: Product,
    cartCount: Int,
    updateCount: (Int, Boolean) -> Unit,
) {

    val inStock = product.stock > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = .5.sp,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$${product.price}",
                modifier = Modifier
                    .padding(top = 1.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }


        Box(modifier = Modifier.weight(1f)){
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .width(50.dp),
                    onClick = { if(cartCount > 1) updateCount(cartCount, false) },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                TextField(
                    modifier = Modifier
                        .width(60.dp),
                    value = "$cartCount",
                    onValueChange = {},
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                FloatingActionButton(
                    modifier = Modifier
                        .width(50.dp),
                    onClick = {  if(product.stock > cartCount) updateCount(cartCount, true) },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()){
        Text(
            text = product.description,
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = ButtonDefaults.MinHeight + 16.dp
                )
                .scrollable(
                    rememberScrollState(),
                    orientation = Orientation.Vertical
                ),
            overflow = TextOverflow.Clip,
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(10.dp),
            enabled = inStock
        ) {

            Text(
                text = if(inStock) "Add to basket" else "Out of stock",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(8.dp))
        }
    }
}

@Composable
fun ProductDetailsAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    AppBar(
        modifier = modifier
            .zIndex(1f)
            .padding(top = 16.dp),
        startIcon = {
            AppBarIcon(
                icon = Icons.AutoMirrored.Default.ArrowBack,
                onClick = { navController.popBackStack() },
                tint = MaterialTheme.colorScheme.surface
            )
        },
        endIcon = {
            AppBarIcon(
                icon = Icons.Default.ShoppingCart,
                onClick = { navController.navigate(AppRoutes.Home.Basket) },
                tint = MaterialTheme.colorScheme.surface
            )
        }
    )
}
