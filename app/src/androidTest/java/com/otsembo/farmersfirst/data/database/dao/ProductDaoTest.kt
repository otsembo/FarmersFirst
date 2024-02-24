package com.otsembo.farmersfirst.data.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.otsembo.farmersfirst.data.database.DBTest
import com.otsembo.farmersfirst.data.database.testProduct
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest: DBTest() {

    private lateinit var productDao: ProductDao

    private suspend fun addProduct() = productDao.create(testProduct).first()

    @Before
    fun setUp() {
        initDB()
        productDao = ProductDao(dbHelper.writableDatabase)
    }

    @Test
    fun testCreateProduct_SuccessfullyAddsProductToDatabase() = runTest {
        val addedProduct = addProduct()
        addedProduct?.let { product ->
            val results = productDao.find(product.id).first()
            assert(results != null) { "Could not add product to database" }
        }
    }

    @Test
    fun testDeleteProduct_SuccessfullyDeletesProductFromDatabase() = runTest {
        val addedProduct = addProduct()
        addedProduct?.let { product ->
            val deleted = productDao.delete(product.id).first()
            val results = productDao.find(product.id).first()
            assert(results == null && deleted){ "Could not remove product from database" }
        }
    }

    @Test
    fun testUpdateProduct_SuccessfullyUpdatesProductInDatabase() = runTest {
        val addedProduct = addProduct()
        val updatedName = "Mower"
        addedProduct?.let { product ->
            val updated = productDao.update(product.copy(name = updatedName), product.id).first()
            val results = productDao.find(product.id).first()
            assert( updated != null && results != null && results.name == updatedName )
        }
    }

    @Test
    fun testFind_SuccessfullyRetrievesProductInDatabase() = runTest {
        val addedProduct = addProduct()
        addedProduct?.let { product ->
            val results = productDao.find(product.id).first()
            assert( results != null ) { "Could not find product in database" }
        }
    }

    @Test
    fun testFindAll_SuccessfullyRetrievesAllProductsInDatabase() = runTest {
        addProduct()
        val results = productDao.findAll().last()
        assert(results.isNotEmpty()){ "Could not find all the products in the database" }
    }

}






