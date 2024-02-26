package com.otsembo.farmersfirst.ui.screens.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.AppUiState
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsScreenVM(
    private val productRepository: IProductRepository,
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
                    productRepository.find(actions.id).collect { res ->
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
        }
    }

}

sealed class ProductDetailsActions {
    data class LoadProduct(val id: Int) : ProductDetailsActions()
    data class CartCountChange(val cartCount: Int): ProductDetailsActions()
}

data class ProductDetailsUiState(
    val cartCount: Int = 1,
    val product: Product? = null,
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
