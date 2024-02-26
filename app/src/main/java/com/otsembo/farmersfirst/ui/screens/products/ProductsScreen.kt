package com.otsembo.farmersfirst.ui.screens.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.otsembo.farmersfirst.R
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.ui.components.AppBar
import com.otsembo.farmersfirst.ui.components.AppBarIcon
import com.otsembo.farmersfirst.ui.components.AppHeading
import com.otsembo.farmersfirst.ui.components.AppNavRail
import com.otsembo.farmersfirst.ui.components.ErrorScreen
import com.otsembo.farmersfirst.ui.components.LoadingScreen
import com.otsembo.farmersfirst.ui.components.NavRailOption
import com.otsembo.farmersfirst.ui.components.SearchField
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme
import kotlin.math.roundToInt

@Composable
fun ProductsScreen(
    modifier: Modifier =  Modifier,
    isWideScreen: Boolean = false,
    viewModel: ProductsScreenVM,
    ) {

    val uiState: ProductsUiState by viewModel.productsUiState.collectAsState()

    if(isWideScreen){

        Row(
            modifier = modifier.fillMaxSize()
        ) {

            AppNavRail(
                navRailOptions = listOf(
                    NavRailOption("Home", icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") }),
                    NavRailOption("Basket", icon = {
                        Box(modifier = Modifier){
                            AppBarIcon(icon = Icons.Default.ShoppingCart)
                            if(uiState.basketItems.isEmpty()) {
                                DotWithText(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    "0",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                DotWithText(
                                    modifier = Modifier.align(Alignment.TopStart),
                                    text = uiState.basketItems.size.toString(),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    })
                )
            )

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        modifier = Modifier
                            .weight(4f),
                        text = "FarmersFirst",
                        style = MaterialTheme.typography.headlineSmall,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    SearchField(
                        label = "Find resources ...",
                        modifier = Modifier
                            .weight(6f),
                        text = uiState.searchTerm,
                        onTextChange = { text -> viewModel.handleActions(ProductsActions.SearchTermChange(text)) },
                    )
                }



                AppHeading(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp),
                    text = "All Items."
                )

                // conditional rendering
                when {
                    uiState.isLoading -> LoadingScreen()
                    uiState.errorOccurred -> ErrorScreen(errorMessage = uiState.errorMessage)
                    uiState.productsList.isNotEmpty() -> {
                        LazyVerticalGrid(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            columns = GridCells.Fixed(4),){
                            items(uiState.productsList){
                                ProductItem(
                                    product = it
                                )
                            }
                        }
                    }
                }

            }

        }

    } else {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AppBar(
                startIcon = {
                    AppBarIcon(icon = Icons.Default.Menu)
                },
                endIcon = {
                    Box(modifier = Modifier){
                        AppBarIcon(icon = Icons.Default.ShoppingCart)
                        if(uiState.basketItems.isEmpty()) {
                            DotWithText(
                                modifier = Modifier.align(Alignment.TopEnd),
                                "0",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            DotWithText(
                                modifier = Modifier.align(Alignment.TopStart),
                                text = uiState.basketItems.size.toString(),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                title =  {
                    Text(
                        text = "FarmersFirst",
                        style = MaterialTheme.typography.headlineSmall,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {

            }

            SearchField(
                label = "Find resources ...",
                modifier = Modifier.fillMaxWidth(),
                text = uiState.searchTerm,
                onTextChange = { text -> viewModel.handleActions(ProductsActions.SearchTermChange(text)) },
                onSubmitSearch =  { viewModel.handleActions(ProductsActions.SubmitSearch) }
                )



            AppHeading(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 24.dp, bottom = 8.dp, start = 8.dp),
                text = "All Items."
            )

            when {
                uiState.isLoading -> LoadingScreen()
                uiState.errorOccurred -> ErrorScreen(errorMessage = uiState.errorMessage)
                uiState.productsList.isNotEmpty() -> {
                    LazyVerticalGrid(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        columns = GridCells.Fixed(2),){

                        items(uiState.productsList){
                            ProductItem(product = it)
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun ProductItem(
    product: Product,
) {

    ElevatedCard(
        modifier = Modifier
            .width(200.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
        ),
        shape = RoundedCornerShape(8.dp)
    ) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)){

            
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.image)
                    .crossfade(true)
                    .build(), contentDescription = product.name,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color(0x69000000), BlendMode.SrcAtop)
            )

            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .size(dimensionResource(id = R.dimen.icon_size))
                    .align(Alignment.TopEnd)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                    .clickable { }
                    .padding(8.dp)
                ,
                imageVector = Icons.Default.AddShoppingCart, contentDescription = "Add to shopping card")

        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 4.dp, end = 4.dp),
            thickness = 1.dp,
        )

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){

            Column() {

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold)

                Text(
                    text = "${product.stock} items left.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Light
                )
            }

            Text(
                text = "$${product.price.roundToInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.ExtraBold
                )

        }
    }

}


@Composable
fun DotWithText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    ) {
        Box(
            modifier = modifier
                .size(16.dp)
                .background(color = color, shape = CircleShape)
                .padding(1.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
            )
        }
}

