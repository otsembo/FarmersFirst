package com.otsembo.farmersfirst.data.repository

import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.data.database.AppDatabaseHelper
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import com.otsembo.farmersfirst.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

/**
 * Interface for the product repository, defining methods for managing product data.
 */
interface IProductRepository {
    /**
     * Adds a new product to the repository.
     * @param product The product to be added.
     * @return A flow of AppResource representing the result of the operation.
     *         The flow emits a nullable Product object upon successful addition,
     *         or null if the operation fails.
     */
    suspend fun addProduct(product: Product): Flow<AppResource<Product?>>

    /**
     * Retrieves all products from the repository.
     * @return A flow of AppResource representing the result of the operation.
     *         The flow emits a list of Product objects upon successful retrieval,
     *         or an error if the operation fails.
     */
    suspend fun showAllProducts(): Flow<AppResource<List<Product>>>

    /**
     * Searches for products in the repository based on the specified search term.
     * @param searchTerm The search term to be used for product search.
     * @return A flow of AppResource representing the result of the operation.
     *         The flow emits a list of Product objects that match the search term,
     *         or an error if the operation fails.
     */
    suspend fun searchProduct(searchTerm: String): Flow<AppResource<List<Product>>>

    /**
     * Finds a product in the repository by its ID.
     * @param id The ID of the product to be found.
     * @return A flow of AppResource representing the result of the operation.
     *         The flow emits a nullable Product object upon successful retrieval,
     *         or null if the product with the specified ID does not exist.
     */
    suspend fun find(id: Int): Flow<AppResource<Product?>>

    /**
     * Finds products in the repository by their IDs.
     * @param id1 The ID of the first product.
     * @param id2 The ID of the second product.
     * @return A flow of AppResource representing the result of the operation.
     *         The flow emits a list of Product objects that match the specified IDs,
     *         or an error if the operation fails.
     */
    suspend fun findProducts(
        id1: Int,
        id2: Int,
    ): Flow<AppResource<List<Product>>>

    /**
     * Updates the stock of a product in the repository after a certain number of items are bought.
     * @param product The product whose stock is to be updated.
     * @param itemsBought The number of items bought.
     * @return A flow of AppResource representing the result of the operation.
     *         The flow emits a Boolean value indicating whether the stock update was successful (true) or not (false).
     */
    suspend fun updateProductStock(
        product: Product,
        itemsBought: Int,
    ): Flow<AppResource<Boolean>>
}

class ProductRepository(
    private val productDao: ProductDao,
) : IProductRepository, BaseRepository() {
    override suspend fun addProduct(product: Product): Flow<AppResource<Product?>> =
        dbTransact(
            productDao.create(product),
        )

    override suspend fun showAllProducts(): Flow<AppResource<List<Product>>> =
        dbTransact(
            productDao.findAll(),
        )

    override suspend fun searchProduct(searchTerm: String): Flow<AppResource<List<Product>>> =
        dbTransact(
            productDao.queryWhere(
                """
                ${AppDatabaseHelper.PRODUCT_NAME} LIKE ? OR ${AppDatabaseHelper.PRODUCT_DESC} LIKE ?
                """.trimIndent(),
                params = arrayOf("%$searchTerm%", "%$searchTerm%"),
            ),
        )

    override suspend fun find(id: Int): Flow<AppResource<Product?>> =
        dbTransact(
            productDao.find(id),
        )

    override suspend fun findProducts(
        id1: Int,
        id2: Int,
    ): Flow<AppResource<List<Product>>> =
        dbTransact(
            productDao.queryWhere(
                """
                ${AppDatabaseHelper.PRODUCT_ID} = ? OR ${AppDatabaseHelper.PRODUCT_ID} = ?
                """.trimIndent(),
                params = arrayOf(id1.toString(), id2.toString()),
            ),
        )

    override suspend fun updateProductStock(
        product: Product,
        itemsBought: Int,
    ): Flow<AppResource<Boolean>> =
        dbTransact(
            flow<Boolean> {
                var stock = product.stock - itemsBought
                if (stock < 0) {
                    stock = 0
                }
                val updatedProduct = product.copy(stock = stock)
                productDao.update(updatedProduct, updatedProduct.id).last()
                emit(true)
            }.catch { emit(false) },
        )
}
