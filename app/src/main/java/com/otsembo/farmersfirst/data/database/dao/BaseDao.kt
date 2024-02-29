package com.otsembo.farmersfirst.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Abstract base class for data access objects (DAOs) providing common CRUD operations on entities in a SQLite database.
 * @param T The type of entity.
 * @property db The SQLiteDatabase instance used for database operations.
 * @property tableName The name of the table associated with the DAO.
 */
abstract class BaseDao<T>(
    private val db: SQLiteDatabase,
    private val tableName: String,
) {
    /**
     * Abstract method to build an entity from a cursor.
     */
    abstract suspend fun Cursor.buildEntity(): T

    /**
     * Abstract method to get content values from an entity.
     */
    abstract fun getContentValues(item: T): ContentValues

    /**
     * Abstract method to set the ID of an entity.
     */
    abstract fun setEntityId(
        item: T,
        id: Int,
    )

    /**
     * Inserts a new item into the database.
     * @param item The item to be inserted.
     * @return A Flow emitting the inserted item if successful, or null otherwise.
     */
    suspend fun create(item: T): Flow<T?> =
        flow {
            val insertId =
                db.insertOrThrow(
                    tableName,
                    null,
                    getContentValues(item),
                )
            if (insertId > 0) {
                setEntityId(item, insertId.toInt())
                emit(item)
            } else {
                emit(null)
            }
        }.catch { emit(null) }

    /**
     * Deletes an item from the database by its ID.
     * @param id The ID of the item to be deleted.
     * @return A Flow emitting true if deletion is successful, or false otherwise.
     */
    suspend fun delete(id: Int): Flow<Boolean> =
        flow {
            val deletedRows =
                db.delete(
                    tableName,
                    "id = ?",
                    arrayOf(id.toString()),
                )
            emit(deletedRows > 0)
        }.catch { emit(false) }

    /**
     * Updates an existing item in the database.
     * @param item The updated item.
     * @param id The ID of the item to be updated.
     * @return A Flow emitting the updated item if successful, or null otherwise.
     */
    suspend fun update(
        item: T,
        id: Int,
    ): Flow<T?> =
        flow {
            val updatedRows =
                db.update(
                    tableName,
                    getContentValues(item),
                    "id = ?",
                    arrayOf(id.toString()),
                )

            if (updatedRows == 1) emitAll(find(id)) else emit(null)
        }.catch { emit(null) }

    /**
     * Finds an item by its ID in the database.
     * @param id The ID of the item to find.
     * @return A Flow emitting the found item if successful, or null otherwise.
     */
    suspend fun find(id: Int): Flow<T?> =
        flow {
            val userCursor =
                db.rawQuery(
                    "SELECT * FROM $tableName WHERE id = ? limit 1",
                    arrayOf(id.toString()),
                )
            if (userCursor.count < 1) {
                emit(null)
                userCursor.close()
            } else {
                while (userCursor.moveToNext()) {
                    emit(userCursor.buildEntity())
                    userCursor.close()
                }
            }
        }

    /**
     * Retrieves all items from the database.
     * @return A Flow emitting a list of all items in the database.
     */
    suspend fun findAll(): Flow<List<T>> =
        flow {
            val users = mutableListOf<T>()
            val usersCursor =
                db.rawQuery(
                    "SELECT * FROM $tableName",
                    null,
                )
            while (usersCursor.moveToNext()) {
                users.add(usersCursor.buildEntity())
            }
            usersCursor.close()
            emit(users)
        }.catch { emit(mutableListOf()) }

    /**
     * Executes a SQL query with a WHERE clause using the provided query string and parameters.
     * @param whereClause The WHERE clause to apply for the query.
     * @param params The array of parameter values to be substituted into the query.
     * @return A flow emitting a list of query results.
     */
    suspend fun queryWhere(
        whereClause: String,
        params: Array<String>,
    ): Flow<List<T>> =
        flow {
            val resultList = mutableListOf<T>()
            val cursor = db.rawQuery("SELECT * FROM $tableName WHERE $whereClause", params)
            while (cursor.moveToNext()) {
                resultList.add(cursor.buildEntity())
            }
            cursor.close()
            emit(resultList)
        }.catch {
            emit(mutableListOf()) // Emit empty list if an error occurs
        }

    /**
     * Deletes rows from the database table based on the specified WHERE clause and parameters.
     * @param whereClause The WHERE clause to apply for the deletion.
     * @param params The parameters to be used in the WHERE clause.
     * @return A flow emitting a boolean value indicating the success or failure of the deletion operation.
     */
    suspend fun deleteWhere(
        whereClause: String,
        params: Array<String>,
    ): Flow<Boolean> =
        flow {
            val affected = db.delete(tableName, whereClause, params)
            emit(affected > 0)
        }.catch { emit(false) }
}
