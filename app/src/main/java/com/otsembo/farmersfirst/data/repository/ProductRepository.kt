package com.otsembo.farmersfirst.data.repository

import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import com.otsembo.farmersfirst.data.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the product repository, defining methods for managing product data.
 */
interface IProductRepository {

    /**
     * Adds a new product to the repository.
     * @param product The product to be added.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun addProduct(product: Product): Flow<AppResource<Product?>>

    /**
     * Retrieves all products from the repository.
     * @return A flow of AppResource representing the result of the operation.
     */
    suspend fun showAllProducts(): Flow<AppResource<List<Product>>>

    suspend fun searchProduct(searchTerm: String): Flow<AppResource<List<Product>>>
}


class ProductRepository(
    private val productDao: ProductDao
): IProductRepository, BaseRepository() {
    override suspend fun addProduct(product: Product): Flow<AppResource<Product?>> =
        dbTransact(productDao.create(product))

    override suspend fun showAllProducts(): Flow<AppResource<List<Product>>> =
        dbTransact(productDao.findAll())

    override suspend fun searchProduct(searchTerm: String): Flow<AppResource<List<Product>>> =
        dbTransact(
            productDao.queryWhere(
                """
                    ${AppDatabaseHelper.PRODUCT_NAME} LIKE ? OR ${AppDatabaseHelper.PRODUCT_DESC} LIKE ?
                """.trimIndent(),
            params = arrayOf("'%$searchTerm%'", "'%$searchTerm%'")
        ))
}