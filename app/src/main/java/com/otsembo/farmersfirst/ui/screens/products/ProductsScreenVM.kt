package com.otsembo.farmersfirst.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.AppUiState
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.repository.IAuthRepository
import com.otsembo.farmersfirst.data.repository.IBasketRepository
import com.otsembo.farmersfirst.data.repository.IProductRepository
import com.otsembo.farmersfirst.data.repository.IUserPrefRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing the state of the products screen.
 *
 * @param productRepository Repository interface for product-related operations.
 * @param basketRepository Repository interface for basket-related operations.
 * @param userPrefRepository Repository interface for user preferences.
 */
class ProductsScreenVM(
    private val productRepository: IProductRepository,
    private val basketRepository: IBasketRepository,
    private val userPrefRepository: IUserPrefRepository,
    private val authRepository: IAuthRepository,
) : ViewModel() {
    // Mutable state flow to represent the products UI state
    private val _productsUiState: MutableStateFlow<ProductsUiState> =
        MutableStateFlow(ProductsUiState())

    // State flow to expose the products UI state
    val productsUiState: StateFlow<ProductsUiState> = _productsUiState

    /**
     * Initializes the ProductsScreenVM and loads all products and basket items.
     */
    init {
        handleActions(ProductsActions.LoadAllProducts)
        handleActions(ProductsActions.LoadBasketItems)
    }

    /**
     * Handles different actions related to products UI.
     *
     * @param actions The action to handle.
     */
    fun handleActions(actions: ProductsActions) {
        when (actions) {
            ProductsActions.LoadAllProducts -> fetchAllProducts()
            is ProductsActions.SearchTermChange ->
                _productsUiState.update {
                    it.copy(searchTerm = actions.newTerm)
                }
            is ProductsActions.AddItemToBasket -> addItemToBasket(actions.productId)
            ProductsActions.SubmitSearch -> searchProduct()
            ProductsActions.LoadBasketItems -> fetchUserBasketItems()
            ProductsActions.SignOutUser -> signOutUser()
        }
    }

    /**
     * Fetches all products.
     */
    private fun fetchAllProducts() {
        viewModelScope.launch {
            productRepository
                .showAllProducts()
                .collect { res -> res.modelProductsUiState() }
        }
    }

    /**
     * Searches for products based on the current search term.
     * If the search term is empty, fetches all products.
     */
    private fun searchProduct() {
        viewModelScope.launch {
            val searchTerm = _productsUiState.value.searchTerm
            if (searchTerm.isNotBlank()) {
                productRepository
                    .searchProduct(searchTerm)
                    .collect { res -> res.modelProductsUiState() }
            } else {
                fetchAllProducts()
            }
            _productsUiState.update {
                it.reset()
                    .copy(
                        productsHeading =
                            if (searchTerm.isNotBlank()) {
                                " '$searchTerm' items "
                            } else {
                                "Best Selling"
                            },
                    )
            }
        }
    }

    /**
     * Fetches basket items for the current user.
     */
    private fun fetchUserBasketItems() {
        viewModelScope.launch {
            userPrefRepository.fetchId().collectLatest { uidRes ->
                when (uidRes) {
                    is AppResource.Error -> _productsUiState.update { it.setError(uidRes.info) }
                    is AppResource.Loading -> _productsUiState.update { it.setLoading() }
                    is AppResource.Success ->
                        basketRepository
                            .fetchLatestBasketItems(uidRes.result)
                            .collect { res -> res.modelBasketItemsAsProductsUiState() }
                }
            }
        }
    }

    /**
     * Adds an item to the user's basket.
     *
     * @param productId The ID of the product to add.
     */
    private fun addItemToBasket(productId: Int) {
        viewModelScope.launch {
            when (val uidRes = userPrefRepository.fetchId().last()) {
                is AppResource.Error -> _productsUiState.update { it.setError(uidRes.info) }
                is AppResource.Loading -> _productsUiState.update { it.setLoading() }
                is AppResource.Success -> {
                    when (val basketRes = basketRepository.fetchLatestBasket(uidRes.result).last()) {
                        is AppResource.Error ->
                            _productsUiState.update {
                                it.setError(basketRes.info)
                            }
                        is AppResource.Loading -> _productsUiState.update { it.setLoading() }
                        is AppResource.Success -> {
                            when {
                                basketRes.result != null -> {
                                    val basketItemsRes =
                                        basketRepository.addItemToBasket(
                                            userId = uidRes.result,
                                            productId = productId,
                                            qty = 1,
                                            basket = basketRes.result,
                                        ).last()

                                    when (basketItemsRes) {
                                        is AppResource.Error ->
                                            _productsUiState.update {
                                                it.setError(basketItemsRes.info)
                                            }
                                        is AppResource.Loading ->
                                            _productsUiState.update {
                                                it.setLoading()
                                            }
                                        is AppResource.Success ->
                                            _productsUiState.update {
                                                it.reset().copy(basketItems = basketItemsRes.result)
                                            }
                                    }
                                }
                                else -> {
                                    _productsUiState.update {
                                        it.setError(
                                            "Something went wrong, please restart your app.",
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Signs out the currently signed-in user.
     * Observes the sign-out operation from the authentication repository and updates the UI state accordingly.
     */
    private fun signOutUser() {
        viewModelScope.launch {
            authRepository.signOutUser().collect { signOutRes ->
                when (signOutRes) {
                    is AppResource.Error -> _productsUiState.update { it.setError(signOutRes.info) }
                    is AppResource.Loading -> _productsUiState.update { it.setLoading() }
                    is AppResource.Success -> {
                        _productsUiState.update {
                            it.reset().copy(isSignedIn = false)
                        }
                    }
                }
            }
        }
    }

    /**
     * Models the UI state based on the success or failure of fetching products.
     */
    private fun AppResource<List<Product>>.modelProductsUiState() {
        flattenProductsUiState { uiState ->
            uiState
                .reset()
                .copy(productsList = (this as AppResource.Success).result)
        }
    }

    /**
     * Models the UI state based on the success or failure of fetching basket items.
     */
    private fun AppResource<List<BasketItem>>.modelBasketItemsAsProductsUiState() {
        flattenProductsUiState { uiState ->
            uiState
                .reset()
                .copy(basketItems = (this as AppResource.Success).result)
        }
    }

    /**
     * Flattens the UI state based on the result of a network call.
     */
    private fun <T> AppResource<T>.flattenProductsUiState(successAction: (ProductsUiState) -> ProductsUiState) {
        when (this) {
            is AppResource.Error -> _productsUiState.update { it.setError(info) }
            is AppResource.Loading -> _productsUiState.update { it.setLoading() }
            is AppResource.Success -> _productsUiState.update { successAction(it) }
        }
    }
}

/**
 * Sealed class representing actions related to products.
 */
sealed class ProductsActions {
    data class SearchTermChange(val newTerm: String) : ProductsActions()

    data object SubmitSearch : ProductsActions()

    data object LoadAllProducts : ProductsActions()

    data object LoadBasketItems : ProductsActions()

    data class AddItemToBasket(val productId: Int) : ProductsActions()

    data object SignOutUser : ProductsActions()
}

/**
 * Data class representing the UI state of the products screen.
 *
 * @param searchTerm The current search term.
 * @param productsList The list of products.
 * @param basketItems The list of items in the basket.
 * @param errorOccurred Boolean indicating if an error occurred.
 * @param isLoading Boolean indicating if data is being loaded.
 * @param errorMessage The error message.
 */
data class ProductsUiState(
    val searchTerm: String = "",
    val productsHeading: String = "Best Selling",
    val productsList: List<Product> = emptyList(),
    val basketItems: List<BasketItem> = emptyList(),
    val isSignedIn: Boolean = true,
    val errorOccurred: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
) : AppUiState<ProductsUiState> {
    /**
     * Resets the UI state.
     */
    override fun reset(): ProductsUiState =
        ProductsUiState(
            searchTerm,
            productsHeading,
            productsList,
            basketItems,
            isSignedIn,
        )

    /**
     * Sets an error state in the UI.
     *
     * @param message The error message.
     */
    override fun setError(message: String): ProductsUiState {
        return reset().copy(errorOccurred = true, errorMessage = message)
    }

    /**
     * Sets the loading state in the UI.
     */
    override fun setLoading(): ProductsUiState {
        return reset().copy(isLoading = true)
    }
}
