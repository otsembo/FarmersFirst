package com.otsembo.farmersfirst.ui.screens.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.AppUiState
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IProductRepository
import com.otsembo.farmersfirst.data.repository.IUserPrefRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsScreenVM(
    private val productRepository: IProductRepository,
    private val userPrefRepository: IUserPrefRepository,
    private val basketRepository: IBasketRepository,
): ViewModel() {

    private val _productDetailsUiState: MutableStateFlow<ProductDetailsUiState> =
        MutableStateFlow(ProductDetailsUiState())
    val productDetailsUiState: StateFlow<ProductDetailsUiState> = _productDetailsUiState



    fun handleActions(actions: ProductDetailsActions){
        when(actions){
            is ProductDetailsActions.CartCountChange -> {
                _productDetailsUiState.update {
                    it.reset().copy(cartCount = actions.cartCount)
                }
            }

            is ProductDetailsActions.LoadProduct -> {
                viewModelScope.launch {
                    productRepository.find(actions.productId).collect { res ->
                        _productDetailsUiState.update {
                            when(res){
                                is AppResource.Error ->
                                    it.reset().copy(errorOccurred = true, errorMessage = res.info)
                                is AppResource.Loading ->
                                    it.reset().copy(isLoading = true)
                                is AppResource.Success ->
                                    it.reset().copy(product = res.data)
                            }
                        }
                    }
                }
            }

            is ProductDetailsActions.AddToCart -> {
                viewModelScope.launch {
                    val userIdRes = userPrefRepository
                        .fetchId()
                        .last()

                    val basketRes = basketRepository
                        .fetchLatestBasket(userId = userIdRes.data ?: 0)
                        .last()


                    when(basketRes){
                        is AppResource.Success -> {
                            val basketItems = basketRepository.addItemToBasket(
                                userIdRes.data ?: 0,
                                basketRes.result!!,
                                productId = actions.productInt,
                                qty = _productDetailsUiState.value.cartCount
                            ).last()

                                when(basketItems) {
                                    is AppResource.Error -> _productDetailsUiState.update {
                                        it.reset()
                                            .copy(errorOccurred = true, errorMessage = basketItems.info)
                                    }
                                    is AppResource.Loading -> _productDetailsUiState.update {
                                        it.reset()
                                            .copy(isLoading = true)
                                    }
                                    is AppResource.Success -> _productDetailsUiState.update {
                                        it.reset()
                                            .copy(
                                                toastCounter = it.toastCounter + 1,
                                                toastMessage = "Item added to basket"
                                            )
                                    }
                                }
                        }
                        else -> _productDetailsUiState.update {
                            it.copy(errorOccurred = true, errorMessage = "An unexpected error has occurred")
                        }
                    }
                }
            }

        }
    }

}

sealed class ProductDetailsActions {
    data class LoadProduct(val productId: Int) : ProductDetailsActions()
    data class CartCountChange(val cartCount: Int): ProductDetailsActions()
    data class AddToCart(val userId: Int, val productInt: Int): ProductDetailsActions()
}

data class ProductDetailsUiState(
    val cartCount: Int = 1,
    val product: Product? = null,
    val toastMessage: String = "",
    var toastCounter: Int = 0,
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = ""
): AppUiState<ProductDetailsUiState>{

    override fun reset(): ProductDetailsUiState =
        ProductDetailsUiState(
            cartCount = cartCount,
            product = product
        )
}
