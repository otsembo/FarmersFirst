package com.otsembo.farmersfirst.ui.navigation

object AppRoutes {
    // root pages
    const val AppAuth = "auth"
    const val AppHome = "home"

    // sub page navigation
    object Home {

        // helpers
        const val productId = "product_id"
        const val userId = "user_id"

        const val Products = "home_products"
        const val Basket = "home_basket"
        const val Checkout = "home_checkout"
        const val ProductDetails = "product_detail/{$productId}"
        const val UserBasket = "home_basket/{$userId}"


        fun productDetails(id: Int): String {
            return ProductDetails.split("{").first() + id
        }
        fun userBasketDetails(id:Int): String {
            return UserBasket.split("{").first() + id
        }
    }

}