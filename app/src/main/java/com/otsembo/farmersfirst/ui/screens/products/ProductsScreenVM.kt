package com.otsembo.farmersfirst.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsScreenVM(
    private val productRepository: IProductRepository,
    private val basketRepository: IBasketRepository,
): ViewModel() {

    private val _productsUiState: MutableStateFlow<ProductsUiState> =
        MutableStateFlow(ProductsUiState())
    val productsUiState: StateFlow<ProductsUiState> = _productsUiState

    init {
        handleActions(ProductsActions.LoadAllProducts)
        handleActions(ProductsActions.LoadBasketItems)
    }

    fun handleActions(actions: ProductsActions){

        when(actions){
            ProductsActions.LoadAllProducts -> {
                viewModelScope.launch {
                    productRepository.showAllProducts().collect { res ->
                        when(res){
                            is AppResource.Error -> {
                                _productsUiState.update { it.copy(
                                    errorOccurred = true,
                                    errorMessage = res.info,
                                    isLoading = false,
                                ) }
                            }
                            is AppResource.Loading -> {
                                _productsUiState.update { it.copy(
                                        isLoading = true,
                                        errorOccurred = false,
                                ) }
                            }
                            is AppResource.Success -> {
                                _productsUiState.update { it.copy(
                                    errorOccurred = false,
                                    isLoading = false,
                                    productsList = res.data ?: emptyList()
                                ) }
                            }
                        }
                    }
                }
            }

            is ProductsActions.SearchTermChange -> {
                _productsUiState.update { it.copy(searchTerm = actions.newTerm) }
            }

            ProductsActions.SubmitSearch -> {
                viewModelScope.launch {
                    productRepository.searchProduct(_productsUiState.value.searchTerm).collect { res ->
                        when(res){
                            is AppResource.Error -> _productsUiState.update {
                                it.reset().copy(errorOccurred = true, errorMessage = res.info)
                            }
                            is AppResource.Loading -> _productsUiState.update {
                                it.reset().copy(isLoading = true)
                            }
                            is AppResource.Success -> _productsUiState.update {
                                it.reset().copy(productsList = res.data ?: emptyList())
                            }
                        }
                    }
                }
            }

            ProductsActions.LoadBasketItems -> {
                viewModelScope.launch {
                    basketRepository.fetchLatestBasketItems(0).collect { res ->
                        when(res){
                            is AppResource.Error -> _productsUiState.update {
                                it.reset().copy(errorOccurred = true, errorMessage = res.info)
                            }
                            is AppResource.Loading -> _productsUiState.update {
                                it.reset().copy(isLoading = true)
                            }
                            is AppResource.Success -> _productsUiState.update {
                                it.reset().copy(basketItems = res.data ?: emptyList())
                            }
                        }
                    }
                }
            }
        }
    }

}


sealed class ProductsActions {
    data class SearchTermChange(val newTerm: String): ProductsActions()
    data object SubmitSearch: ProductsActions()
    data object LoadAllProducts: ProductsActions()
    data object LoadBasketItems: ProductsActions()
}

data class ProductsUiState(
    val searchTerm: String = "",
    val isLoading: Boolean = false,
    val errorOccurred: Boolean =  false,
    val errorMessage: String = "null",
    val productsList: List<Product> = emptyList(),
    val basketItems: List<BasketItem> = emptyList()
){
    fun reset(): ProductsUiState = ProductsUiState(
        searchTerm, isLoading = false, errorOccurred = false, errorMessage, productsList, basketItems
    )
}