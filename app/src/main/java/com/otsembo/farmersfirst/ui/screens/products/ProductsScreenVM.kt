package com.otsembo.farmersfirst.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.AppUiState
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.Basket
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.model.User
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IProductRepository
import com.otsembo.farmersfirst.data.repository.IUserPrefRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsScreenVM(
    private val productRepository: IProductRepository,
    private val basketRepository: IBasketRepository,
    private val userPrefRepository: IUserPrefRepository,
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

            is ProductsActions.AddItemToBasket -> {
                viewModelScope.launch {
                    when(val uidRes = userPrefRepository.fetchId().last()){
                        is AppResource.Error -> _productsUiState.update {
                                it.reset().copy(errorOccurred = true, errorMessage = uidRes.info)
                            }
                        is AppResource.Loading -> _productsUiState.update {
                                it.reset().copy(isLoading = true)
                            }
                        is AppResource.Success -> {
                            when(val latestBasket = basketRepository.fetchLatestBasket(uidRes.result).last()){
                                is AppResource.Error -> _productsUiState.update {
                                    it.reset().copy(errorOccurred = true, errorMessage = latestBasket.info)
                                }
                                is AppResource.Loading -> _productsUiState.update {
                                    it.reset().copy(isLoading = true)
                                }
                                is AppResource.Success -> {
                                    when {
                                        latestBasket.result != null -> {
                                            val basketItems = basketRepository.addItemToBasket(
                                                userId = uidRes.result,
                                                productId = actions.productId,
                                                qty = 1,
                                                basket = latestBasket.result
                                            ).last()

                                            when(basketItems){
                                                is AppResource.Error -> _productsUiState.update {
                                                    it.reset().copy(errorOccurred = true, errorMessage = basketItems.info)
                                                }
                                                is AppResource.Loading -> _productsUiState.update {
                                                    it.reset().copy(isLoading = true)
                                                }
                                                is AppResource.Success -> {
                                                    _productsUiState.update {
                                                        it
                                                            .reset()
                                                            .copy(
                                                                basketItems = basketItems.result,
                                                                toastMessage = "Item added to basket",
                                                                toastCounter = it.toastCounter + 1
                                                            )
                                                    }
                                                }
                                            }
                                        }
                                        else -> {
                                            _productsUiState.update { it.reset().copy(errorOccurred = true, errorMessage = "Something went wrong, please restart your app.") }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            ProductsActions.SubmitSearch -> {
                viewModelScope.launch {
                    val searchTerm = _productsUiState.value.searchTerm
                    if(searchTerm.isNotBlank())
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
                    else handleActions(ProductsActions.LoadAllProducts)
                }
            }

            ProductsActions.LoadBasketItems -> {
                viewModelScope.launch {
                    userPrefRepository.fetchId().map { uidRes ->
                        when(uidRes){
                            is AppResource.Error -> _productsUiState.update {
                                it.reset().copy(errorOccurred = true, errorMessage = uidRes.info)
                            }
                            is AppResource.Loading -> _productsUiState.update {
                                it.reset().copy(isLoading = true)
                            }
                            is AppResource.Success -> basketRepository
                                .fetchLatestBasketItems(uidRes.result).collect { res ->
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
    }

}


sealed class ProductsActions {
    data class SearchTermChange(val newTerm: String): ProductsActions()
    data object SubmitSearch: ProductsActions()
    data object LoadAllProducts: ProductsActions()
    data object LoadBasketItems: ProductsActions()
    data class AddItemToBasket(val productId: Int): ProductsActions()
}

data class ProductsUiState(
    val searchTerm: String = "",
    val productsList: List<Product> = emptyList(),
    val basketItems: List<BasketItem> = emptyList(),
    val toastMessage: String = "",
    var toastCounter: Int = 0,
    val errorOccurred: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
): AppUiState<ProductsUiState>{
    override fun reset(): ProductsUiState =
        ProductsUiState(
            searchTerm = searchTerm,
            productsList = productsList,
            basketItems = basketItems
        )

    override fun setError(message: String): ProductsUiState {
        return reset().copy(errorOccurred = true, errorMessage = message)
    }

    override fun setLoading(): ProductsUiState {
        return reset().copy(isLoading = true)
    }
}