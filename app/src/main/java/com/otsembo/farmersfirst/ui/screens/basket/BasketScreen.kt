package com.otsembo.farmersfirst.ui.screens.basket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.otsembo.farmersfirst.R
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.Basket
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.model.User
import com.otsembo.farmersfirst.ui.components.AppBar
import com.otsembo.farmersfirst.ui.components.EmptyEntityMessage
import com.otsembo.farmersfirst.ui.screens.product_details.CartCounter
import kotlin.math.roundToInt

@Composable
fun BasketScreen(
    modifier: Modifier = Modifier,
    isWideScreen: Boolean = false,
    viewModel: BasketScreenVM,
    navController: NavHostController,
    userId: Int = 0,
) {

    val basketScreenUiState: BasketScreenUiState by viewModel.basketScreenUiState.collectAsState()

    LaunchedEffect(key1 = basketScreenUiState.basketItems){
        viewModel.handleActions(BasketScreenActions.LoadBasketItems(userId))
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {

        AppBar(
            modifier = Modifier.padding(top = 8.dp),
            startIcon = {
                Icon(
                    modifier = Modifier.clickable { navController.popBackStack() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            },
            title = {
                Text(text = "Your Basket")
            }
        )

        when {
            basketScreenUiState.basketItems.isEmpty() -> {
                EmptyEntityMessage(
                    modifier = Modifier
                        .fillMaxHeight(0.68f)
                        .fillMaxSize(),
                    message = "Your cart is currently empty"
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.68f)
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    items(basketScreenUiState.basketItems){
                        BasketItemUi(
                            basketItem = it,
                            onUpdateItemCount = { direction ->
                                viewModel.handleActions(BasketScreenActions.UpdateBasketItemCount(
                                    basketItem = it, direction
                                ))
                            }
                        )
                    }
                }
            }
        }



        ElevatedCard(
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {

            Box(modifier = Modifier
                .fillMaxSize()) {


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CheckoutSummary(
                        modifier = Modifier.padding(vertical = 4.dp),
                        startText = "Total Cost:",
                        endText = "$${basketScreenUiState.totalBasketCost}"
                    )
                    CheckoutSummary(
                        modifier = Modifier.padding(vertical = 8.dp),
                        startText = "Discount:",
                        endText = "$${basketScreenUiState.totalBasketDiscount}"
                    )

                    HorizontalDivider()

                    CheckoutSummary(
                        modifier = Modifier.padding(vertical = 8.dp),
                        startText = "Total:",
                        endText = "$${basketScreenUiState.totalBasketCost - basketScreenUiState.totalBasketDiscount}",
                        isSubSummary = false,
                    )
                }



                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    Text(
                        text = "Checkout",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                }

            }



        }

    }


}


@Composable
fun BasketItemUi(
    basketItem: BasketItem = BasketItem(
        0,
        Basket(0, User(0, ""), AppDatabaseHelper.BasketStatusPending),
        Product(0, "Succulent", "", 13, 120.0f, ""),
        12
    ),
    onUpdateItemCount: (BasketScreenActions.BasketUpdateDirection) -> Unit
) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            AsyncImage(
                modifier = Modifier.weight(1f),
                model = basketItem.product.image,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = basketItem.product.name,
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${basketItem.product.stock} remaining",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                CartCounter(
                    modifier = Modifier
                        .fillMaxWidth(),
                    actionModifier = Modifier.size(30.dp),
                    inputModifier = Modifier.width(30.dp),
                    inputShape = RoundedCornerShape(20),
                    cartCount = basketItem.quantity,
                    productStock = basketItem.product.stock,
                    updateCount = { _, isIncrease ->  onUpdateItemCount(
                        if (isIncrease) BasketScreenActions.BasketUpdateDirection.UP else BasketScreenActions.BasketUpdateDirection.DOWN
                    )}
                )

                Text(
                    text = "$${(basketItem.product.price * basketItem.quantity).roundToInt()}",
                    modifier = Modifier
                        .padding(top = 4.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

            }

        }

    }

}


@Composable
fun CheckoutSummary(
    modifier: Modifier = Modifier,
    isSubSummary: Boolean = true,
    startText: String,
    endText: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if(isSubSummary){
            Text(
                text = startText,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = endText,
                style = MaterialTheme.typography.bodyLarge,
            )
        }else{
            Text(
                text = startText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = endText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
