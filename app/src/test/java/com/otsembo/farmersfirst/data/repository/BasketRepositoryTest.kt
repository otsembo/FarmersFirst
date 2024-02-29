package com.otsembo.farmersfirst.data.repository

import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.database.dao.BasketDao
import com.otsembo.farmersfirst.data.database.dao.BasketItemDao
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import com.otsembo.farmersfirst.testBasket
import com.otsembo.farmersfirst.testBasketItem
import com.otsembo.farmersfirst.testProduct
import com.otsembo.farmersfirst.testUser
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class BasketRepositoryTest {
    // Mock dependencies
    @MockK
    lateinit var productDao: ProductDao

    @MockK
    lateinit var basketDao: BasketDao

    @MockK
    lateinit var basketItemDao: BasketItemDao

    @MockK
    lateinit var appDatabaseHelper: AppDatabaseHelper

    private lateinit var basketRepository: BasketRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        basketRepository = BasketRepository(productDao, basketDao, basketItemDao)
    }

    @Test
    fun `test createBasket`() =
        runBlocking {
            // Mock dependencies behavior
            coEvery { basketDao.create(testBasket) } returns flowOf(testBasket)
            // Call the method under test
            val result = basketRepository.createBasket(testBasket).last()
            // Verify the result
            assertEquals(AppResource.Success(testBasket), result)
        }

    @Test
    fun `test updateBasket`() =
        runBlocking {
            // Mock dependencies behavior
            coEvery { basketDao.update(testBasket, testBasket.id) } returns flowOf(testBasket)
            // Call the method under test
            val result = basketRepository.updateBasket(testBasket).last()
            // Verify the result
            assertEquals(AppResource.Success(testBasket), result)
        }

    @Test
    fun `test fetchLatestBasketItems`() =
        runBlocking {
            // Mock dependencies behavior
            coEvery { productDao.find(testProduct.id) } returns flowOf(testProduct)
            coEvery { basketDao.queryWhere(any(), any()) } returns flowOf(listOf(testBasket))
            coEvery {
                basketItemDao.queryWhere(any(), any())
            } returns flowOf(listOf(testBasketItem))
            // Call the method under test
            val result = basketRepository.fetchLatestBasketItems(1).last()
            // Verify the result
            assertEquals(AppResource.Success(listOf(testBasketItem)), result)
        }

    @Test
    fun `test addItemToBasket`() =
        runBlocking {
            // Mock dependencies behavior
            coEvery { productDao.find(testProduct.id) } returns flowOf(testProduct)
            coEvery { basketDao.create(testBasket) } returns flowOf(testBasket)
            coEvery { basketItemDao.create(testBasketItem) } returns flowOf(testBasketItem)
            coEvery { basketDao.queryWhere(any(), any()) } returns flowOf(listOf(testBasket))
            coEvery {
                basketItemDao.queryWhere(any(), any())
            } returns flowOf(listOf(testBasketItem))
            coEvery {
                basketRepository.fetchLatestBasketItems(testUser.id)
            } returns flowOf(AppResource.Success(listOf(testBasketItem)))
            // Call the method under test
            val result = basketRepository.addItemToBasket(1, testBasket, 1, 1).last()
            // Verify the result
            assertEquals(AppResource.Success(listOf(testBasketItem)), result)
        }

    @Test
    fun `test removeItemFromBasket`() =
        runBlocking {
            // Mock dependencies behavior
            coEvery { basketItemDao.delete(any()) } returns flowOf(true)
            // Call the method under test
            val result = basketRepository.removeItemFromBasket(testBasketItem).last()
            // Verify the result
            assertEquals(AppResource.Success(true), result)
        }

    @Test
    fun `test updateBasketItem`() =
        runBlocking {
            // Mock dependencies behavior
            coEvery { basketItemDao.update(any(), any()) } returns flowOf(testBasketItem)
            // Call the method under test
            val result = basketRepository.updateBasketItem(testBasketItem).last()
            // Verify the result
            assertEquals(AppResource.Success(testBasketItem), result)
        }
}
