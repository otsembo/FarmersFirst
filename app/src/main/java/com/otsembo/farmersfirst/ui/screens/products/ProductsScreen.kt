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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun ProductsScreen(
    modifier: Modifier =  Modifier,
    isWideScreen: Boolean = false
    ) {

    if(isWideScreen){

        Row(
            modifier = modifier.fillMaxSize()
        ) {

            AppNavRail(
                navRailOptions = listOf(
                    NavRailOption("Home", icon = Icons.Default.Home),
                    NavRailOption("Basket", icon = Icons.Default.ShoppingBasket)
                )
            )

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                SearchField(
                    label = "Find resources ...",
                    modifier = Modifier
                        .fillMaxWidth(0.5f))

                AppHeading(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp),
                    text = "All Items."
                )
                
                ErrorScreen(errorMessage = "You are being hacked")

                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(4),){

                    items((1..30).toList()){
                        ProductItem()
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
                    AppBarIcon(icon = Icons.Default.ShoppingCart)
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {

            }

            SearchField(label = "Find resources ...", modifier = Modifier.fillMaxWidth())


            AppHeading(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 24.dp, bottom = 8.dp, start = 8.dp),
                text = "All Items."
            )

            LoadingScreen()

            LazyVerticalGrid(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Fixed(2),){

                items((1..30).toList()){
                    ProductItem()
                }

            }

        }
    }
}


@Preview
@Composable
fun ProductItem(
    product: Product = Product(0, "Farm supplies", "Something", 20, 12.0f, "")
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

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                painter = painterResource(id = R.drawable.auth_background),
                contentDescription = "",
                colorFilter = ColorFilter.tint(Color(0x69000000), BlendMode.SrcAtop),
                contentScale = ContentScale.Crop
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

            Text(
                text = "Dulacha",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer)

            Text(
                text = "$23.00",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.ExtraBold
                )

        }
    }

}


