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
                                is AppResource.Error -> _basketScreenUiState.update {
                                    it.reset().copy(errorOccurred = true, errorMessage = basketItemsRes.info)
                                }
                                is AppResource.Loading -> _basketScreenUiState.update {
                                    it.reset().copy(isLoading = true)
                                }
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
        }
    }

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
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = ""
): AppUiState<BasketScreenUiState> {
    override fun reset(): BasketScreenUiState {
        return BasketScreenUiState(
            basketItems, totalBasketCost, totalBasketDiscount
        )
    }
}

sealed class BasketScreenActions {
    data class LoadBasketItems(val userId: Int): BasketScreenActions()
    data class CalculateBasketTotals(val basketItems: List<BasketItem>): BasketScreenActions()
}