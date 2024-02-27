package com.otsembo.farmersfirst.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.notNull
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.model.Basket
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

abstract class BaseRecommenderRepository <T> {

    val recommenderModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BasketRepository.GEMINI_API_KEY
    )
    abstract suspend fun textRecommend(input: String): Flow<AppResource<T>>
}

class BasketItemsRecommenderRepository(
    private val productRepository: IProductRepository,
    private val basketRepository: IBasketRepository,
    private val userPrefRepository: IUserPrefRepository,

) : BaseRecommenderRepository<List<BasketItem>>() {
    override suspend fun textRecommend(input: String): Flow<AppResource<List<BasketItem>>> =
        flow {
            emit(AppResource.Loading())
            val response = recommenderModel.generateContent(input)
            println("RecommenderRepository: ${response.text}")
            if (response.text.notNull()){
                when(val productsRes = productRepository.showAllProducts().last()){
                    is AppResource.Success -> {
                        val userId = userPrefRepository.fetchId().last().data ?: 0
                        val userBasket = basketRepository
                            .fetchLatestBasket(userId)
                            .last()
                            .data ?: Basket(user = User(email = ""), status = AppDatabaseHelper.BasketStatusPending)
                        val basketItems = productsRes.result.map {
                            BasketItem(
                                id = 0,
                                basket = userBasket,
                                quantity = 1,
                                product = it
                            )
                        }
                        emit(AppResource.Success(result = basketItems))
                    }
                    is AppResource.Error -> throw Exception(productsRes.info)
                    is AppResource.Loading -> emit(AppResource.Loading())
                }
            }else{
                emit(AppResource.Success(result = emptyList()))
            }
        }.catch { emit(AppResource.Error(info = it.message ?: "Could not fetch recommendations")) }
}
