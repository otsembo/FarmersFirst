package com.otsembo.farmersfirst.ui.navigation

object AppRoutes {
    // root pages
    const val AppAuth = "auth"
    const val AppHome = "home"

    // sub page navigation
    object Home {

        // helpers
        const val productId = "product_id"

        const val Products = "home_products"
        const val Basket = "home_basket"
        const val Checkout = "home_checkout"
        const val ProductDetails = "product_detail/{$productId}"


        fun productDetails(id: Int): String {
            return ProductDetails.split("{").first() + id
        }
    }

}