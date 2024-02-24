package com.otsembo.farmersfirst.data.repository

import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import com.otsembo.farmersfirst.testProduct
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductRepositoryTest {

    @Test
    fun `test addProduct`() = runBlocking {
        // Mock ProductDao
        val productDao = mockk<ProductDao>()
        val productRepository = ProductRepository(productDao)
        val expectedResult = flowOf(AppResource.Success(testProduct))

        // Mock behavior of productDao.create
        coEvery { productDao.create(testProduct) } returns flowOf(testProduct)

        // Call the function under test
        val resultFlow = productRepository.addProduct(testProduct)

        // Verify interaction with ProductDao
        assertEquals(expectedResult.last(), resultFlow.last())
    }

    @Test
    fun `test showAllProducts`() = runBlocking {
        // Mock ProductDao
        val productDao = mockk<ProductDao>()
        val productRepository = ProductRepository(productDao)

        val productList = listOf(
            testProduct.copy(id = 1, name = "Wrench"),
            testProduct.copy(id = 2, name = "Wheel barrow"),
            testProduct.copy(id = 3, name = "Spade"),
            testProduct.copy(id = 4, name = "Other Spade"),

        )
        val expectedResult = flowOf(AppResource.Success(productList))

        // Mock behavior of productDao.findAll
        coEvery { productDao.findAll() } returns flowOf(productList)

        // Call the function under test
        val resultFlow = productRepository.showAllProducts()

        // Verify interaction with ProductDao
        assertEquals(expectedResult.last(), resultFlow.last())
    }
}