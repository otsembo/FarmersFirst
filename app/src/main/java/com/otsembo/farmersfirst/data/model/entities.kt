package com.otsembo.farmersfirst.data.model

/**
 * Data class representing a user entity.
 * @property id The unique identifier of the user (default value: 0).
 * @property email The email address of the user.
 */
data class User(
    var id: Int = 0,
    val email: String,
)

/**
 * Data class representing a product entity.
 * @property id The unique identifier of the product (default value: 0).
 * @property name The name of the product.
 * @property description The description of the product.
 * @property stock The stock quantity of the product.
 * @property price The price of an individual product
 * @property image The image URL of an individual product
 */
data class Product(
    var id: Int = 0,
    val name: String,
    val description: String,
    val stock: Int,
    val price: Float,
    val image: String,
)

/**
 * Data class representing a basket entity.
 * @property id The unique identifier of the basket (default value: 0).
 * @property user The user associated with the basket.
 * @property status The status of the basket.
 */
data class Basket(
    var id: Int = 0,
    val user: User,
    val status: String,
)

/**
 * Data class representing a basket item entity.
 * @property id The unique identifier of the basket item (default value: 0).
 * @property basket The basket associated with the item.
 * @property product The product associated with the item.
 * @property quantity The quantity of the product in the basket.
 */
data class BasketItem(
    var id: Int = 0,
    val basket: Basket,
    val product: Product,
    val quantity: Int,
)
