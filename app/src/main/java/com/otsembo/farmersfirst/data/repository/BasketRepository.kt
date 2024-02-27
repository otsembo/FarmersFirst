package com.otsembo.farmersfirst.data.repository

import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.izNull
import com.otsembo.farmersfirst.common.notNull
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.database.dao.BasketDao
import com.otsembo.farmersfirst.data.database.dao.BasketItemDao
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import com.otsembo.farmersfirst.data.model.Basket
import com.otsembo.farmersfirst.data.model.BasketItem
import com.otsembo.farmersfirst.data.model.Product
import com.otsembo.farmersfirst.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map

/**
 * Interface for the basket repository, defining methods for managing baskets and basket items.
 */
interface IBasketRepository {

    /**
     * Creates a new basket in the repository.
     * @param basket The basket to be created.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun createBasket(basket: Basket): Flow<AppResource<Basket?>>

    /**
     * Updates an existing basket in the repository.
     * @param basket The basket to be updated.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun updateBasket(basket: Basket): Flow<AppResource<Basket?>>

    /**
     * Fetches the latest basket items for a given user ID.
     * @param userId The ID of the user whose basket items are to be fetched.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun fetchLatestBasketItems(userId: Int): Flow<AppResource<List<BasketItem>>>

    suspend fun fetchLatestBasket(userId: Int): Flow<AppResource<Basket?>>

    /**
     * Adds an item to the basket for a given user ID.
     * @param userId The ID of the user whose basket is to be updated.
     * @param basket The basket to which the item is to be added.
     * @param productId The ID of the product to be added to the basket.
     * @param qty The quantity of the product to be added.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun addItemToBasket(userId: Int, basket: Basket, productId: Int, qty: Int): Flow<AppResource<List<BasketItem>>>

    /**
     * Removes an item from the basket.
     * @param basketItemId The ID of the basket item to be removed.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun removeItemFromBasket(basketItemId: Int): Flow<AppResource<Boolean>>

    /**
     * Updates an existing basket item in the repository.
     * @param basketItem The basket item to be updated.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun updateBasketItem(basketItem: BasketItem): Flow<AppResource<BasketItem?>>
}

/**
 * Repository class responsible for handling basket-related operations,
 * such as creating, updating, and retrieving basket items.
 * Implements the [IBasketRepository] interface.
 *
 * @param productDao The data access object for products.
 * @param basketDao The data access object for baskets.
 * @param basketItemDao The data access object for basket items.
 */
class BasketRepository(
    private val productDao: ProductDao,
    private val basketDao: BasketDao,
    private val basketItemDao: BasketItemDao,
): IBasketRepository, BaseRepository(){
    override suspend fun createBasket(basket: Basket): Flow<AppResource<Basket?>> =
        dbTransact(basketDao.create(basket))

    override suspend fun updateBasket(basket: Basket): Flow<AppResource<Basket?>> =
        dbTransact(basketDao.update(basket, basket.id))

    override suspend fun fetchLatestBasket(userId: Int): Flow<AppResource<Basket?>>  =
        dbTransact(
            flow {
                val baskets = basketDao.queryWhere(
                    query = """
                    ${AppDatabaseHelper.BASKET_USER} = ? AND 
                    ${AppDatabaseHelper.BASKET_STATUS} = ? 
                    ORDER BY ${AppDatabaseHelper.BASKET_ID} DESC LIMIT 1
                    """.trimIndent(),
                    params = arrayOf(userId.toString(), AppDatabaseHelper.BasketStatusPending)
                ).last()

                if(baskets.isEmpty()){
                    emit(null)
                } else {
                    emit(baskets.last())
                }
            })

    override suspend fun fetchLatestBasketItems(userId: Int): Flow<AppResource<List<BasketItem>>> =
        dbTransact(flow{
            val baskets = basketDao.queryWhere(
                query = """
                ${AppDatabaseHelper.BASKET_USER} = ? AND 
                ${AppDatabaseHelper.BASKET_STATUS} = ? 
                ORDER BY ${AppDatabaseHelper.BASKET_ID} DESC LIMIT 1
                """.trimIndent(),
                params = arrayOf(userId.toString(), AppDatabaseHelper.BasketStatusPending)
            )
            emitAll(baskets.map {
                if(it.isEmpty()){
                    createBasket(
                        basket = Basket(
                            user = User(userId, email = ""),
                            status = AppDatabaseHelper.BasketStatusPending
                        )
                    ).last()
                    fetchLatestBasketItems(userId).last().data ?: emptyList()
                }
                else {
                    val basketId = it.last().id
                    val basketItemsList = basketItemDao.queryWhere(
                        query = """
                            ${AppDatabaseHelper.BASKET_ITEM_BASKET} = ?
                        """.trimIndent(),
                        params = arrayOf("$basketId")
                    )
                    basketItemsList.last()
                }
            })
        })

    override suspend fun addItemToBasket(userId: Int, basket: Basket, productId: Int, qty: Int):
            Flow<AppResource<List<BasketItem>>> = dbTransact(flow{
                val product = productDao.find(productId).last()
                if(product.notNull()) {
                    val basketItem = basketItemDao
                        .create(item = BasketItem(0, basket, product!!, qty))
                        .last()
                    basketItem ?: throw Exception("Could not add item to basket")
                }
                emitAll(
                    fetchLatestBasketItems(userId).map {
                        it.data ?: emptyList()
                    }
                )
            })

    override suspend fun removeItemFromBasket(basketItemId: Int): Flow<AppResource<Boolean>> =
        dbTransact(basketItemDao.delete(basketItemId))

    override suspend fun updateBasketItem(basketItem: BasketItem): Flow<AppResource<BasketItem?>> =
        dbTransact(basketItemDao.update(basketItem, basketItem.id))

}