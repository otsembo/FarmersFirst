package com.otsembo.farmersfirst.data.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.otsembo.farmersfirst.data.database.DBTest
import com.otsembo.farmersfirst.data.database.testBasket
import com.otsembo.farmersfirst.data.database.testUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class BasketDaoTest: DBTest() {

    private lateinit var basketDao: BasketDao
    private lateinit var userDao: UserDao

    private suspend fun addUser() = userDao.create(testUser).first()
    private suspend fun addBasket() = basketDao.create(testBasket).first()

    @Before
    fun setUp() {
        initDB()
        userDao = UserDao(dbHelper.writableDatabase)
        basketDao = BasketDao(dbHelper.writableDatabase, userDao)
    }

    @Test
    fun testCreateBasket_SuccessfullyAddsBasketToDatabase() = runTest {
        addUser()
        val addedBasket = addBasket()
        addedBasket?.let { basket ->
            val results = basketDao.find(basket.id).first()
            assert(results != null) { "Could not add basket to database" }
        }
    }

    @Test
    fun testDeleteBasket_SuccessfullyDeletesBasketFromDatabase() = runTest {
        addUser()
        val addedBasket = addBasket()
        addedBasket?.let { basket ->
            val deleted = basketDao.delete(basket.id).first()
            val results = basketDao.find(basket.id).first()
            assert(results == null && deleted){ "Could not remove basket from database" }
        }
    }

    @Test
    fun testUpdateBasket_SuccessfullyUpdatesBasketInDatabase() = runTest {
        addUser()
        val addedBasket = addBasket()
        val updatedStatus = "complete"
        addedBasket?.let { basket ->
            val updated = basketDao.update(basket.copy(status = updatedStatus), basket.id).first()
            val results = basketDao.find(basket.id).first()
            assert( updated != null && results != null && results.status == updatedStatus )
        }
    }

    @Test
    fun testFind_SuccessfullyRetrievesBasketInDatabase() = runTest {
        addUser()
        val addedBasket = addBasket()
        addedBasket?.let { basket ->
            val results = basketDao.find(basket.id).first()
            assert( results != null ) { "Could not find basket in database" }
        }
    }

    @Test
    fun testFindAll_SuccessfullyRetrievesAllBasketsInDatabase() = runTest {
        addUser()
        addBasket()
        val results = basketDao.findAll().last()
        assert(results.isNotEmpty()){ "Could not find all the baskets in the database" }
    }

}