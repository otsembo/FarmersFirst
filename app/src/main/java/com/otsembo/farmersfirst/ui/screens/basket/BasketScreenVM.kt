package com.otsembo.farmersfirst.ui.screens.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.AppUiState
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IUserPrefRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BasketScreenVM(
    private val basketRepository: IBasketRepository,
    private val userPrefRepository: IUserPrefRepository,
) : ViewModel() {

    private val _basketScreenUiState = MutableStateFlow(BasketScreenUiState())
    val basketScreenUiState: StateFlow<BasketScreenUiState> = _basketScreenUiState

    fun handleActions(action: BasketScreenActions){
        when(action){

            is BasketScreenActions.LoadBasketItems -> {
                viewModelScope.launch {
                    basketRepository
                        .fetchLatestBasketItems(if(action.userId == 0) fetchUid() else action.userId)
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


            is BasketScreenActions.CalculateBasketTotals -> {
                var totalCost = 0f
                action.basketItems.forEach { basketItem -> totalCost += (basketItem.product.price * basketItem.quantity) }
                _basketScreenUiState.update { it.reset().copy(totalBasketCost = totalCost) }
            }


            is BasketScreenActions.UpdateBasketItemCount -> {
                viewModelScope.launch {
                    val updateBasketRes = basketRepository
                        .updateBasketItem(
                            action.basketItem.copy(
                                quantity = if(action.direction == BasketScreenActions.BasketUpdateDirection.UP)
                                    action.basketItem.quantity + 1
                                else
                                    action.basketItem.quantity - 1
                            )
                        )
                        .last()
                    when(updateBasketRes){
                        is AppResource.Error -> _basketScreenUiState.update { it.setError(updateBasketRes.info)}
                        is AppResource.Loading -> _basketScreenUiState.update { it.setLoading() }
                        is AppResource.Success -> handleActions(BasketScreenActions.LoadBasketItems(userId = 0))
                    }
                }
            }


            is BasketScreenActions.ToggleRecommendedProducts -> {
                _basketScreenUiState.update { it.reset().copy( showRecommender = action.show ) }
            }

        }
    }

    /*val items = res.result.map { item -> Pair(item.id, item.name) }

    recommenderRepository.textRecommend("""
                                    given the following items: $items
                                    suggest two items for someone who has shopped the following: ${items.last()}.
                                    provide the answer in this format [id1, id2]
                                """.trimIndent()).collect {
        when(it){
            is AppResource.Error -> println("ProductsRepo (recommender err): ${it.info}")
            is AppResource.Loading -> println("ProductsRepo (recommender loading):")
            is AppResource.Success -> println("ProductsRepo suggestions: ${it.result}")
        }
    }*/

    private suspend fun fetchUid(): Int {
        val uidRes = userPrefRepository.fetchId().last()
        return uidRes.data ?: 0
    }

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

data class BasketScreenUiState(
    val basketItems: List<BasketItem> = emptyList(),
    val totalBasketCost: Float = 0.0f,
    val totalBasketDiscount: Float = 0.0f,
    val showRecommender: Boolean = false,
    val recommendedBasketItems: List<BasketItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = ""
): AppUiState<BasketScreenUiState> {
    override fun reset(): BasketScreenUiState {
        return BasketScreenUiState(
            basketItems,
            totalBasketCost,
            totalBasketDiscount,
            showRecommender,
            recommendedBasketItems
        )
    }
    override fun setError(message: String): BasketScreenUiState {
        return reset().copy(errorOccurred = true, errorMessage = message)
    }

    override fun setLoading(): BasketScreenUiState {
        return reset().copy(isLoading = true)
    }
}

sealed class BasketScreenActions {
    data class LoadBasketItems(val userId: Int): BasketScreenActions()
    data class CalculateBasketTotals(val basketItems: List<BasketItem>): BasketScreenActions()
    data class UpdateBasketItemCount(val basketItem: BasketItem, val direction: BasketUpdateDirection): BasketScreenActions()
    data class ToggleRecommendedProducts(val show: Boolean): BasketScreenActions()
    enum class BasketUpdateDirection {
        UP, DOWN
    }

}