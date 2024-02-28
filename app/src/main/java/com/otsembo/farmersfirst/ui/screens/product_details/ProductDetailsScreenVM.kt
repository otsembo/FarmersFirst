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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing the product details screen state.
 *
 * @param productRepository Repository interface for product-related operations.
 * @param userPrefRepository Repository interface for user preferences.
 * @param basketRepository Repository interface for basket-related operations.
 */
class ProductDetailsScreenVM(
    private val productRepository: IProductRepository,
    private val userPrefRepository: IUserPrefRepository,
    private val basketRepository: IBasketRepository,
) : ViewModel() {

    private val _productDetailsUiState: MutableStateFlow<ProductDetailsUiState> =
        MutableStateFlow(ProductDetailsUiState())
    val productDetailsUiState: StateFlow<ProductDetailsUiState> = _productDetailsUiState


    /**
     * Function to handle different actions related to product details.
     *
     * @param actions The product details action to be handled.
     */
    fun handleActions(actions: ProductDetailsActions){
        when(actions){
            is ProductDetailsActions.CartCountChange ->
                _productDetailsUiState.update { it.reset().copy(cartCount = actions.cartCount) }

            is ProductDetailsActions.LoadProduct -> loadProduct(actions.productId)
            is ProductDetailsActions.AddToCart -> addItemToCart(actions.productId)
        }
    }


    private fun loadProduct(productId: Int){
        viewModelScope.launch {
            productRepository.find(productId).collect { res ->
                _productDetailsUiState.update {
                    when(res){
                        is AppResource.Error -> it.setError(res.info)
                        is AppResource.Loading -> it.setLoading()
                        is AppResource.Success ->
                            it.reset().copy(product = res.data)
                    }
                }
            }
        }
    }

    private fun addItemToCart(productId: Int) {
        viewModelScope.launch {
            val userIdRes = userPrefRepository
                .fetchId()
                .last()

            val basketRes = basketRepository
                .fetchLatestBasket(userId = userIdRes.data ?: 0)
                .last()

            when(basketRes){
                is AppResource.Success -> {
                    val basketItemsRes = basketRepository.addItemToBasket(
                        userIdRes.data ?: 0,
                        basketRes.result!!,
                        productId = productId,
                        qty = _productDetailsUiState.value.cartCount).last()

                    _productDetailsUiState.update {
                        when(basketItemsRes){
                            is AppResource.Error -> it.setError(basketItemsRes.info)
                            is AppResource.Loading -> it.setLoading()
                            is AppResource.Success -> it.copy()
                        }
                    }
                }
                is AppResource.Loading -> _productDetailsUiState.update { it.setLoading() }
                is AppResource.Error -> _productDetailsUiState.update {
                    it.setError("An unexpected error has occurred")
                }
            }
        }
    }

}


/**
 * Sealed class representing actions related to product details screen.
 */
sealed class ProductDetailsActions {
    /**
     * Action to load a product with the specified ID.
     *
     * @param productId The ID of the product to load.
     */
    data class LoadProduct(val productId: Int) : ProductDetailsActions()

    /**
     * Action to change the cart count for a product.
     *
     * @param cartCount The new cart count.
     */
    data class CartCountChange(val cartCount: Int): ProductDetailsActions()

    /**
     * Action to add a product to the cart for the specified user.
     *
     * @param userId The ID of the user.
     * @param productId The ID of the product to add to the cart.
     */
    data class AddToCart(val userId: Int, val productId: Int): ProductDetailsActions()
}


/**
 * Data class representing the UI state of product details screen.
 *
 * @param cartCount The count of items in the cart.
 * @param product The product being displayed.
 * @param isLoading Flag indicating whether data is currently being loaded.
 * @param errorOccurred Flag indicating whether an error has occurred.
 * @param errorMessage Error message to display if an error has occurred.
 */
data class ProductDetailsUiState(
    val cartCount: Int = 1,
    val product: Product? = null,
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = ""
): AppUiState<ProductDetailsUiState>{

    /**
     * Resets the UI state.
     *
     * @return A new [ProductDetailsUiState] instance with default values.
     */
    override fun reset(): ProductDetailsUiState =
        ProductDetailsUiState(
            cartCount = cartCount,
            product = product
        )

    /**
     * Sets an error state with the given error message.
     *
     * @param message The error message to set.
     * @return A new [ProductDetailsUiState] instance with the error state.
     */
    override fun setError(message: String): ProductDetailsUiState {
        return reset().copy(errorOccurred = true, errorMessage = message)
    }

    /**
     * Sets the loading state.
     *
     * @return A new [ProductDetailsUiState] instance with the loading state.
     */
    override fun setLoading(): ProductDetailsUiState {
        return reset().copy(isLoading = true)
    }
}

