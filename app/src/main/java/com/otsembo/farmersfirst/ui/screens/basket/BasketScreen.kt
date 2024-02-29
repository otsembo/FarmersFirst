package com.otsembo.farmersfirst.ui.screens.basket

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.ui.components.AppBar
import com.otsembo.farmersfirst.ui.components.EmptyEntityMessage
import com.otsembo.farmersfirst.ui.components.ErrorScreen
import com.otsembo.farmersfirst.ui.components.LoadingScreen
import com.otsembo.farmersfirst.ui.navigation.AppRoutes
import com.otsembo.farmersfirst.ui.screens.productDetails.CartCounter
import kotlin.math.roundToInt

/**
 * Composable function to display the basket screen UI.
 *
 * @param modifier Modifier for styling the component.
 * @param isWideScreen Flag indicating if the screen width is wide.
 * @param viewModel ViewModel for managing the basket screen data.
 * @param navController NavHostController for navigation within the application.
 * @param userId The ID of the current user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketScreen(
    modifier: Modifier = Modifier,
    isWideScreen: Boolean = false,
    viewModel: BasketScreenVM,
    navController: NavHostController,
    userId: Int = 0,
) {
    val basketScreenUiState: BasketScreenUiState by viewModel.basketScreenUiState.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()

    LaunchedEffect(key1 = basketScreenUiState.basketItems) {
        viewModel.handleActions(BasketScreenActions.LoadBasketItems(userId))
    }

    LaunchedEffect(basketScreenUiState.navigateToCheckout, block = {
        if (basketScreenUiState.navigateToCheckout) {
            navController.navigate(AppRoutes.Home.Checkout)
            viewModel.handleActions(BasketScreenActions.OnCheckoutNavigationComplete)
        }
    })

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        AppBar(
            modifier = Modifier.padding(top = 8.dp),
            startIcon = {
                Icon(
                    modifier = Modifier.clickable { navController.popBackStack() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            },
            title = {
                Text(text = "Your Basket")
            },
        )

        when {
            basketScreenUiState.basketItems.isEmpty() -> {
                EmptyEntityMessage(
                    modifier =
                        Modifier
                            .fillMaxHeight(0.68f)
                            .fillMaxSize(),
                    message = "Your cart is currently empty",
                )
            }
            basketScreenUiState.isLoading -> LoadingScreen()
            basketScreenUiState.errorOccurred ->
                ErrorScreen(
                    errorMessage = basketScreenUiState.errorMessage,
                )
            else -> {
                if (isWideScreen) {
                    Row {
                        LazyColumn(
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.68f)
                                    .padding(top = 8.dp)
                                    .padding(start = 16.dp, end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            items(basketScreenUiState.basketItems) { item ->
                                BasketItemUi(
                                    basketItem = item,
                                    onUpdateItemCount = { direction ->
                                        viewModel.handleActions(
                                            BasketScreenActions.UpdateBasketItemCount(
                                                basketItem = item,
                                                direction,
                                            ),
                                        )
                                    },
                                    onDeleteItemFromBasket = {
                                        viewModel.handleActions(
                                            BasketScreenActions.DeleteBasketItem(it),
                                        )
                                    },
                                )
                            }
                        }

                        ElevatedCard(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                            ) {
                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                ) {
                                    CheckoutSummary(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        startText = "Total Cost:",
                                        endText = "$${basketScreenUiState.totalBasketCost}",
                                    )
                                    CheckoutSummary(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        startText = "Discount:",
                                        endText = "$${basketScreenUiState.totalBasketDiscount}",
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
                                    onClick = {
                                        viewModel.handleActions(
                                            BasketScreenActions.MakeRecommendations,
                                        )
                                    },
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomStart)
                                            .padding(horizontal = 16.dp)
                                            .padding(bottom = 8.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = basketScreenUiState.basketItems.isNotEmpty(),
                                ) {
                                    Text(
                                        text = "Checkout",
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxHeight(0.6f)
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                                .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(basketScreenUiState.basketItems) { basketItem ->
                            BasketItemUi(
                                basketItem = basketItem,
                                onUpdateItemCount = { direction ->
                                    viewModel.handleActions(
                                        BasketScreenActions.UpdateBasketItemCount(
                                            basketItem = basketItem,
                                            direction,
                                        ),
                                    )
                                },
                                onDeleteItemFromBasket = {
                                    viewModel.handleActions(
                                        BasketScreenActions.DeleteBasketItem(it),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }

        if (!isWideScreen) {
            ElevatedCard(
                modifier =
                    Modifier
                        .fillMaxSize(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                    ) {
                        CheckoutSummary(
                            modifier = Modifier.padding(vertical = 4.dp),
                            startText = "Total Cost:",
                            endText = "$${basketScreenUiState.totalBasketCost}",
                        )
                        CheckoutSummary(
                            modifier = Modifier.padding(vertical = 8.dp),
                            startText = "Discount:",
                            endText = "$${basketScreenUiState.totalBasketDiscount}",
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
                        onClick = {
                            viewModel.handleActions(BasketScreenActions.MakeRecommendations)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = basketScreenUiState.basketItems.isNotEmpty(),
                    ) {
                        Text(
                            text = "Checkout",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }

    if (basketScreenUiState.showRecommender) {
        CheckoutRecommender(
            modifier = Modifier.fillMaxHeight(if (isWideScreen) 1f else 0.5f),
            recommenderSheetState = bottomSheetState,
            onRecommenderClose = {
                viewModel.handleActions(BasketScreenActions.ToggleRecommendedProducts(false))
            },
            recommendedBasketItems = basketScreenUiState.recommendedBasketItems,
            navController = navController,
            onCheckOut = {
                viewModel.handleActions(BasketScreenActions.CheckoutItems)
            },
            onDeleteItemFromRecommendedBasket = {
                viewModel.handleActions(BasketScreenActions.DeleteRecommendedItem(0))
            },
        )
    }
}

/**
 * Composable function to display a single item in the basket.
 *
 * @param disableUpdates Flag to disable updates for the basket item.
 * @param basketItem The basket item to be displayed.
 * @param onUpdateItemCount Callback to update the item count.
 * @param onDeleteItemFromBasket Callback to delete the item from the basket.
 */
@Composable
fun BasketItemUi(
    disableUpdates: Boolean = false,
    basketItem: BasketItem,
    onUpdateItemCount: (BasketScreenActions.BasketUpdateDirection) -> Unit,
    onDeleteItemFromBasket: (BasketItem) -> Unit,
) {
    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(125.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AsyncImage(
                modifier = Modifier.weight(1f),
                model = basketItem.product.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier =
                    Modifier
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
                    fontWeight = FontWeight.Light,
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                // show counter if updates are not disable
                if (!disableUpdates) {
                    CartCounter(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        actionModifier = Modifier.size(30.dp),
                        inputModifier = Modifier.width(30.dp),
                        inputShape = RoundedCornerShape(20),
                        cartCount = basketItem.quantity,
                        productStock = basketItem.product.stock,
                        updateCount = { _, isIncrease ->
                            onUpdateItemCount(
                                if (isIncrease) BasketScreenActions.BasketUpdateDirection.UP else BasketScreenActions.BasketUpdateDirection.DOWN,
                            )
                        },
                    )
                }

                Text(
                    text = "$${(basketItem.product.price * basketItem.quantity).roundToInt()}",
                    modifier =
                        Modifier
                            .align(if (disableUpdates) Alignment.End else Alignment.Start)
                            .padding(top = 4.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                )

                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Delete Item",
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .clickable { onDeleteItemFromBasket(basketItem) },
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

/**
 * Composable function to display a summary in the checkout screen.
 *
 * @param modifier Modifier for styling the component.
 * @param isSubSummary Flag indicating if the summary is a sub-summary.
 * @param startText The text to be displayed at the start of the summary.
 * @param endText The text to be displayed at the end of the summary.
 */
@Composable
fun CheckoutSummary(
    modifier: Modifier = Modifier,
    isSubSummary: Boolean = true,
    startText: String,
    endText: String,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSubSummary) {
            Text(
                text = startText,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = endText,
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            Text(
                text = startText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )

            Text(
                text = endText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        }
    }
}

/**
 * Composable function to display the checkout recommender modal bottom sheet.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param recommenderSheetState The state of the bottom sheet.
 * @param recommendedBasketItems The list of recommended basket items.
 * @param onRecommenderClose Callback to close the recommender sheet.
 * @param navController The NavHostController for navigation.
 * @param onCheckOut Callback to trigger the checkout action.
 * @param onDeleteItemFromRecommendedBasket Callback to delete an item from the recommended basket.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutRecommender(
    modifier: Modifier = Modifier,
    recommenderSheetState: SheetState,
    recommendedBasketItems: List<BasketItem> = emptyList(),
    onRecommenderClose: () -> Unit = {},
    navController: NavHostController,
    onCheckOut: () -> Unit = {},
    onDeleteItemFromRecommendedBasket: (Int) -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier.fillMaxSize(),
        sheetState = recommenderSheetState,
        onDismissRequest = { onRecommenderClose() },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        dragHandle = {},
    ) {
        if (recommendedBasketItems.isNotEmpty()) {
            Box(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
            ) {
                Button(
                    onClick = {
                        onCheckOut()
                    },
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = "Add to basket\nand checkout",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                OutlinedButton(
                    onClick = {
                        onCheckOut()
                        navController.navigate(AppRoutes.Home.Checkout)
                    },
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(text = "Just Checkout", style = MaterialTheme.typography.bodyLarge)
                }

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "The following items are usually purchased together.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                        )
                    }

                    itemsIndexed(recommendedBasketItems.take(2)) { index, basketItem ->
                        BasketItemUi(
                            disableUpdates = true,
                            onUpdateItemCount = { },
                            basketItem = basketItem,
                            onDeleteItemFromBasket = { onDeleteItemFromRecommendedBasket(index) },
                        )
                    }
                }
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                EmptyEntityMessage(
                    modifier = Modifier.weight(1f),
                    message = "We have no recommendations for you.",
                )
                Button(
                    onClick = {
                        onCheckOut()
                        navController.navigate(AppRoutes.Home.Checkout)
                    },
                    modifier =
                        Modifier
                            .height(ButtonDefaults.MinHeight + 8.dp)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(text = "Checkout Now", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
