package com.otsembo.farmersfirst.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

abstract class BaseDao<T> (val db: SQLiteDatabase) {
    abstract suspend fun create(item: T): Flow<T?>
    abstract suspend fun delete(item: T): Flow<Boolean>
    abstract suspend fun update(item: T): Flow<T?>
    abstract suspend fun find(id: Int): Flow<T?>
    abstract suspend fun findAll(): Flow<List<T>>

}

data class User(
    var id: Int = 0,
    val email: String
)

class UserDao(db: SQLiteDatabase) : BaseDao<User>(db){

    override suspend fun create(item: User): Flow<User?> = flow<User?> {
        val values = ContentValues().apply {
            put(AppDatabaseHelper.USER_EMAIL, item.email)
        }
        db.insertOrThrow(AppDatabaseHelper.TABLE_USERS, null, values)
        emit(item)
    }.catch { emit(null) }

    override suspend fun delete(item: User): Flow<Boolean> =
        flow {
            db.delete(AppDatabaseHelper.TABLE_USERS, "id = ?", arrayOf(item.id.toString()))
            emit(true)
        }.catch { emit(false) }


    override suspend fun update(item: User): Flow<User?> =
        flow {
            db.delete(AppDatabaseHelper.TABLE_USERS, "id = ?", arrayOf(item.id.toString()))
            emitAll(find(item.id))
        }.catch { emit(null) }

    override suspend fun find(id: Int): Flow<User?> = flow {
        val userCursor = db.rawQuery("SELECT * FROM ${AppDatabaseHelper.TABLE_USERS} WHERE id = ? limit 1", arrayOf(id.toString()))
        if(userCursor.count < 1){
            emit(null)
            userCursor.close()
        }else{
            while (userCursor.moveToNext()){
                emit(userCursor.getUser())
                userCursor.close()
            }
        }
    }

    override suspend fun findAll(): Flow<List<User>> =
        flow {
            val users = mutableListOf<User>()
            val usersCursor = db.rawQuery("SELECT * FROM ${AppDatabaseHelper.TABLE_USERS}", null)
            while (usersCursor.moveToNext()){
                users.add(usersCursor.getUser())
            }
            usersCursor.close()
            emit(users)
        }.catch { emit(mutableListOf()) }

}

fun Cursor.getUser(): User {
    val id = getInt(0)
    val email = getString(1)
    return User(id, email)
}
