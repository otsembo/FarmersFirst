package com.otsembo.farmersfirst.data.database.dao

import kotlinx.coroutines.flow.Flow

/**
 * Interface defining common CRUD operations for any entity type.
 * @param T The type of entity.
 */
interface BaseDao<T> {
    /**
     * Inserts a new item into the database.
     * @param item The item to be inserted.
     * @return A Flow emitting the inserted item if successful, or null otherwise.
     */
    suspend fun create(item: T): Flow<T?>

    /**
     * Deletes an item from the database.
     * @param item The item to be deleted.
     * @return A Flow emitting true if deletion is successful, or false otherwise.
     */
    suspend fun delete(item: T): Flow<Boolean>

    /**
     * Updates an existing item in the database.
     * @param item The updated item.
     * @return A Flow emitting the updated item if successful, or null otherwise.
     */
    suspend fun update(item: T): Flow<T?>

    /**
     * Finds an item by its ID in the database.
     * @param id The ID of the item to find.
     * @return A Flow emitting the found item if successful, or null otherwise.
     */
    suspend fun find(id: Int): Flow<T?>

    /**
     * Retrieves all items from the database.
     * @return A Flow emitting a list of all items in the database.
     */
    suspend fun findAll(): Flow<List<T>>
}
