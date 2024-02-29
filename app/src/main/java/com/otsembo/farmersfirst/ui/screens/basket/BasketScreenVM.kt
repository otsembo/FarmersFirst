package com.otsembo.farmersfirst.ui.screens.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.AppUiState
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.Basket
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.User
import com.otsembo.farmersfirst.data.repository.BasketItemsRecommenderRepository
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IProductRepository
import com.otsembo.farmersfirst.data.repository.IUserPrefRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BasketScreenVM(
    private val basketRepository: IBasketRepository,
    private val userPrefRepository: IUserPrefRepository,
    private val recommenderRepository: BasketItemsRecommenderRepository,
    private val productsRepository: IProductRepository,
) : ViewModel() {

    private val _basketScreenUiState = MutableStateFlow(BasketScreenUiState())
    val basketScreenUiState: StateFlow<BasketScreenUiState> = _basketScreenUiState

    /**
     * Handles the given [action] performed by the user on the basket screen.
     *
     * This method dispatches the action to the appropriate handler method based on its type.
     *
     * @param action The action to be handled.
     */
    fun handleActions(action: BasketScreenActions){
        when(action){
            is BasketScreenActions.LoadBasketItems -> loadBasketItems(action.userId)
            is BasketScreenActions.CalculateBasketTotals -> {
                var totalCost = 0f
                action.basketItems.forEach { basketItem -> totalCost += (basketItem.product.price * basketItem.quantity) }
                _basketScreenUiState.update { it.reset().copy(totalBasketCost = totalCost) }
            }
            is BasketScreenActions.UpdateBasketItemCount -> updateBasketItemCount(action.basketItem, action.direction)
            is BasketScreenActions.ToggleRecommendedProducts -> {
                _basketScreenUiState.update { it.reset().copy( showRecommender = action.show ) }
            }
            is BasketScreenActions.MakeRecommendations -> makeRecommendation()
            is BasketScreenActions.CheckoutItems -> checkoutItems()
            is BasketScreenActions.DeleteBasketItem -> deleteBasketItem(action.basketItem)
            is BasketScreenActions.DeleteRecommendedItem -> deleteRecommendedBasketItem(action.index)
            is BasketScreenActions.OnCheckoutNavigationComplete -> _basketScreenUiState.update {
                it.reset().copy(navigateToCheckout = false)
            }
        }
    }


    /**
     * Loads the basket items for the specified [userId] from the repository.
     *
     * This method retrieves the latest basket items from the repository and updates the UI state accordingly.
     *
     * @param userId The ID of the user whose basket items to load.
     */
    private fun loadBasketItems(userId: Int){
        viewModelScope.launch {
            basketRepository
                .fetchLatestBasketItems(if(userId == 0) fetchUid() else userId)
                .collectLatest { basketItemsRes ->
                    when(basketItemsRes){
                        is AppResource.Error -> _basketScreenUiState.update { it.setError(basketItemsRes.info) }
                        is AppResource.Loading -> _basketScreenUiState.update { it.setLoading() }
                        is AppResource.Success -> _basketScreenUiState.update {
                            val aggregatedBasket = aggregateBasketProducts(basketItemsRes.result)
                            handleActions(BasketScreenActions.CalculateBasketTotals(aggregatedBasket))
                            it.reset().copy(basketItems = aggregatedBasket)
                        }
                    }
                }
        }
    }

    /**
     * Updates the quantity of a basket item and reloads the basket items from the repository.
     *
     * This method updates the quantity of the specified [basketItem] based on the [direction]
     * (either increase or decrease) and reloads the basket items from the repository.
     *
     * @param basketItem The basket item to update.
     * @param direction The direction of the update (UP for increase, DOWN for decrease).
     */
    private fun updateBasketItemCount(basketItem: BasketItem, direction: BasketScreenActions.BasketUpdateDirection){
        viewModelScope.launch {
            val updateBasketRes = basketRepository
                .updateBasketItem(
                    basketItem.copy(
                        quantity = if(direction == BasketScreenActions.BasketUpdateDirection.UP)
                            basketItem.quantity + 1
                        else
                            basketItem.quantity - 1
                    )
                ).last()

            when(updateBasketRes){
                is AppResource.Error -> _basketScreenUiState.update { it.setError(updateBasketRes.info)}
                is AppResource.Loading -> _basketScreenUiState.update { it.setLoading() }
                is AppResource.Success -> handleActions(BasketScreenActions.LoadBasketItems(userId = fetchUid()))
            }
        }
    }

    /**
     * Makes recommendations for additional products to add to the basket based on the current items.
     *
     * This method retrieves the list of products in the basket, requests recommendations from
     * the recommender repository, and updates the UI state with the recommended products.
     */
    private fun makeRecommendation(){
        val productsInBasket = _basketScreenUiState.value.basketItems.map { basketItem -> basketItem.product }
        viewModelScope.launch {
            val products = productsRepository.showAllProducts().last().data ?: emptyList()
            recommenderRepository
                .textRecommend(
                    """
                        given the following items: $products
                        suggest two items for someone who has already shopped the following: ${productsInBasket}.
                        provide the answer in this format [id1, id2]
                    """.trimIndent()).collect { recommendedBasketRes ->
                    when(recommendedBasketRes){
                        is AppResource.Error -> _basketScreenUiState.update {
                            handleActions(BasketScreenActions.ToggleRecommendedProducts(true))
                            it.reset()
                        }
                        is AppResource.Loading -> _basketScreenUiState.update {
                            it.setLoading()
                        }
                        is AppResource.Success -> {
                            _basketScreenUiState.update { it.reset().copy(recommendedBasketItems = recommendedBasketRes.result) }
                            handleActions(BasketScreenActions.ToggleRecommendedProducts(true))
                        }
                    }
                }
        }
    }

    /**
     * Checks out the items in the basket, updating the user's basket status and product stock.
     *
     * This method finalizes the user's basket by updating its status, creating a new basket for future
     * purchases, and resetting the stock of items in the database.
     */
    private fun checkoutItems(){
        handleActions(BasketScreenActions.ToggleRecommendedProducts(show = false))
        _basketScreenUiState.update {
            it.setLoading()
        }
        viewModelScope.launch {
            // update user basket and create new one for future
            val userId = fetchUid()
            val userBasket = basketRepository
                .fetchLatestBasket(userId)
                .last().data ?: Basket(0, User(userId, "email"), AppDatabaseHelper.BasketStatusPending)
            basketRepository.updateBasket(userBasket.copy(status = AppDatabaseHelper.BasketStatusChecked)).last()
            basketRepository.createBasket(userBasket).last()

            // reset db items
            for (item in _basketScreenUiState.value.basketItems){
                productsRepository.updateProductStock(item.product, item.quantity).last()
            }

            // manual delay to simulate checking out
            delay(2000)

            // navigate to checkout page
            _basketScreenUiState.update {
                it.reset().copy(navigateToCheckout = true)
            }
        }

    }

    /**
     * Deletes the specified basket item from the repository and updates the UI accordingly.
     * @param basketItem The basket item to be deleted.
     */
    private fun deleteBasketItem(basketItem: BasketItem) {
        viewModelScope.launch {
            val deletedBasketItem = basketRepository
                .removeItemFromBasket(basketItem)
                .last()
            if (deletedBasketItem.data == true) {
                handleActions(BasketScreenActions.LoadBasketItems(fetchUid()))
            } else {
                _basketScreenUiState.update {
                    it.reset().setError(message = "An error occurred when deleting the item from basket")
                }
            }
        }
    }

    /**
     * Deletes the recommended basket item at the specified index from the UI state.
     * @param itemIndex The index of the recommended basket item to be deleted.
     */
    private fun deleteRecommendedBasketItem(itemIndex: Int) {
        _basketScreenUiState.update { screenState ->
            val updatedRecommendedList = screenState
                .recommendedBasketItems
                .toMutableList()
            updatedRecommendedList.removeAt(itemIndex)
            screenState.copy(recommendedBasketItems = updatedRecommendedList)
        }
    }


    /**
     * Fetches the user ID from the user preferences repository.
     *
     * This method retrieves the user ID from the user preferences repository and returns it.
     *
     * @return The user ID fetched from the repository.
     */
    private suspend fun fetchUid(): Int {
        val uidRes = userPrefRepository.fetchId().last()
        return uidRes.data ?: 0
    }

    /**
     * Aggregates the basket items by grouping them based on the product and summing their quantities.
     *
     * This method takes a list of basket items, groups them by product, and calculates the total quantity
     * for each product. It returns a new list of aggregated basket items.
     *
     * @param basketItems The list of basket items to aggregate.
     * @return The aggregated list of basket items.
     */
    private fun aggregateBasketProducts(basketItems: List<BasketItem>): List<BasketItem> {
        val aggregatedBasketItems = basketItems
            .groupBy { basketItem -> basketItem.product }
            .map { groupEntry ->
                val mergedBasketItem = groupEntry.value
                    .reduce { acc, basketItem -> basketItem.copy(quantity = acc.quantity + basketItem.quantity) }
                mergedBasketItem
            }
        return aggregatedBasketItems
    }


}

/**
 * Represents the UI state of the basket screen.
 *
 * This data class encapsulates various properties that represent the state of the basket screen,
 * including the list of basket items, total basket cost, total basket discount, visibility of the
 * recommender section, recommended basket items, loading state, error state, and error message.
 *
 * @property basketItems The list of basket items.
 * @property totalBasketCost The total cost of the basket.
 * @property totalBasketDiscount The total discount applied to the basket.
 * @property showRecommender Flag indicating whether the recommender section is visible.
 * @property recommendedBasketItems The list of recommended basket items.
 * @property navigateToCheckout Flag indicating whether we should navigate to checkout page
 * @property isLoading Flag indicating whether data is being loaded.
 * @property errorOccurred Flag indicating whether an error occurred.
 * @property errorMessage The error message if an error occurred.
 */
data class BasketScreenUiState(
    val basketItems: List<BasketItem> = emptyList(),
    val totalBasketCost: Float = 0.0f,
    val totalBasketDiscount: Float = 0.0f,
    val showRecommender: Boolean = false,
    val recommendedBasketItems: List<BasketItem> = emptyList(),
    val navigateToCheckout: Boolean = false,
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = ""
) : AppUiState<BasketScreenUiState> {

    /**
     * Resets the UI state to its initial values.
     *
     * This method creates a new instance of [BasketScreenUiState] with default values for all properties
     * except [basketItems] and [recommendedBasketItems], which are retained from the current state.
     *
     * @return A new instance of [BasketScreenUiState] with default values.
     */
    override fun reset(): BasketScreenUiState {
        return BasketScreenUiState(
            basketItems = basketItems,
            recommendedBasketItems = recommendedBasketItems,
            totalBasketCost = totalBasketCost,
            totalBasketDiscount = totalBasketDiscount,
            showRecommender = showRecommender,
            navigateToCheckout = navigateToCheckout
        )
    }

    /**
     * Sets the UI state to indicate an error with the specified [message].
     *
     * This method creates a new instance of [BasketScreenUiState] with [errorOccurred] set to true
     * and [errorMessage] set to the specified [message]. All other properties are retained from the current state.
     *
     * @param message The error message.
     * @return A new instance of [BasketScreenUiState] with error state.
     */
    override fun setError(message: String): BasketScreenUiState {
        return reset().copy(errorOccurred = true, errorMessage = message)
    }

    /**
     * Sets the UI state to indicate that data is being loaded.
     *
     * This method creates a new instance of [BasketScreenUiState] with [isLoading] set to true.
     * All other properties are retained from the current state.
     *
     * @return A new instance of [BasketScreenUiState] with loading state.
     */
    override fun setLoading(): BasketScreenUiState {
        return reset().copy(isLoading = true)
    }
}

/**
 * Represents the actions that can be performed on the basket screen.
 *
 * This sealed class defines various actions that can be triggered on the basket screen,
 * including loading basket items, calculating basket totals, updating basket item count,
 * toggling recommended products, making recommendations, and checking out items.
 */
sealed class BasketScreenActions {

    /**
     * Action to load basket items for the specified [userId].
     *
     * @property userId The ID of the user for whom basket items are to be loaded.
     */
    data class LoadBasketItems(val userId: Int): BasketScreenActions()

    /**
     * Action to calculate basket totals based on the provided [basketItems].
     *
     * @property basketItems The list of basket items for which totals are to be calculated.
     */
    data class CalculateBasketTotals(val basketItems: List<BasketItem>): BasketScreenActions()

    /**
     * Action to update the count of a specific [basketItem] in the basket, based on the specified [direction].
     *
     * @property basketItem The basket item to be updated.
     * @property direction The direction of the update (UP or DOWN).
     */
    data class UpdateBasketItemCount(val basketItem: BasketItem, val direction: BasketUpdateDirection): BasketScreenActions()

    /**
     * Action to toggle the visibility of recommended products.
     *
     * @property show Flag indicating whether to show recommended products.
     */
    data class ToggleRecommendedProducts(val show: Boolean): BasketScreenActions()

    /**
     * Action to make recommendations based on the current basket items.
     */
    data object MakeRecommendations : BasketScreenActions()

    /**
     * Action to check out items in the basket.
     */
    data object CheckoutItems: BasketScreenActions()

    /**
     * Represents an action to delete a recommended item from the list.
     * @property index The index of the recommended item to be deleted.
     */
    data class DeleteRecommendedItem(val index: Int): BasketScreenActions()

    /**
     * Represents an action to delete a basket item.
     * @property basketItem The basket item to be deleted.
     */
    data class DeleteBasketItem(val basketItem: BasketItem): BasketScreenActions()

    /**
     * Represents an action indicating that the checkout navigation has been completed.
     */
    data object OnCheckoutNavigationComplete: BasketScreenActions()

    /**
     * Enumeration representing the direction of basket item count update (UP or DOWN).
     */
    enum class BasketUpdateDirection {
        UP, DOWN
    }
}
