package com.otsembo.farmersfirst

import com.otsembo.farmersfirst.data.model.Basket
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.model.User

val testUser = User(1, "email@mail.com")
val testProduct = Product(1, "fertilizer", "long fertilizer", 20, 12.5f, "")
val testBasket = Basket(id = 1, user = testUser, status = "pending")
val testBasketItem = BasketItem(id = 1, basket = testBasket, product = testProduct, 2)
