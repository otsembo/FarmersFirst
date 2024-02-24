package com.otsembo.farmersfirst.data.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.otsembo.farmersfirst.data.database.DBTest
import com.otsembo.farmersfirst.data.database.testBasket
import com.otsembo.farmersfirst.data.database.testBasketItem
import com.otsembo.farmersfirst.data.database.testProduct
import com.otsembo.farmersfirst.data.database.testUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasketItemDaoTest: DBTest() {

    private lateinit var basketItemDao: BasketItemDao
    private lateinit var basketDao: BasketDao
    private lateinit var productDao: ProductDao
    private lateinit var userDao: UserDao

    private suspend fun addUser() = userDao.create(testUser).first()
    private suspend fun addBasketItem() = basketItemDao.create(testBasketItem).first()
    private suspend fun addProduct() = productDao.create(testProduct).first()
    private suspend fun addBasket() = basketDao.create(testBasket).first()

    @Before
    fun setUp() {
        initDB()
        userDao = UserDao(dbHelper.writableDatabase)
        productDao = ProductDao(dbHelper.writableDatabase)
        basketDao = BasketDao(dbHelper.writableDatabase, userDao)
        basketItemDao = BasketItemDao(dbHelper.writableDatabase, basketDao, productDao)
    }

    @Test
    fun testCreateBasketItem_SuccessfullyAddsBasketItemToDatabase() = runTest {
        addUser()
        addProduct()
        addBasket()
        val addedBasketItem = addBasketItem()
        addedBasketItem?.let { basketItem ->
            val results = basketItemDao.find(basketItem.id).first()
            assert(results != null) { "Could not add basketItem to database" }
        }
    }

    @Test
    fun testDeleteBasketItem_SuccessfullyDeletesBasketItemFromDatabase() = runTest {
        addUser()
        addProduct()
        addBasket()
        val addedBasketItem = addBasketItem()
        addedBasketItem?.let { basketItem ->
            val deleted = basketItemDao.delete(basketItem.id).first()
            val results = basketItemDao.find(basketItem.id).first()
            assert(results == null && deleted){ "Could not remove basketItem from database" }
        }
    }

    @Test
    fun testUpdateBasketItem_SuccessfullyUpdatesBasketItemInDatabase() = runTest {
        addUser()
        addProduct()
        addBasket()
        val addedBasketItem = addBasketItem()
        val updatedQty = 4
        addedBasketItem?.let { basketItem ->
            val updated = basketItemDao.update(basketItem.copy(quantity = updatedQty), basketItem.id).first()
            val results = basketItemDao.find(basketItem.id).first()
            assert( updated != null && results != null && results.quantity == updatedQty ){ "Could not update basket item in database" }
        }
    }

    @Test
    fun testFind_SuccessfullyRetrievesBasketItemInDatabase() = runTest {
        addUser()
        addProduct()
        addBasket()
        val addedBasketItem = addBasketItem()
        addedBasketItem?.let { basketItem ->
            val results = basketItemDao.find(basketItem.id).first()
            assert( results != null ) { "Could not find basket item in database" }
        }
    }

    @Test
    fun testFindAll_SuccessfullyRetrievesAllBasketItemsInDatabase() = runTest {
        addUser()
        addProduct()
        addBasket()
        addBasketItem()
        val results = basketItemDao.findAll().last()
        assert(results.isNotEmpty()){ "Could not find all the basket items in the database" }
    }


}