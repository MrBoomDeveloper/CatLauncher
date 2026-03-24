package com.mrboomdev.catlauncher.data.db.dao

import androidx.room3.*
import com.mrboomdev.catlauncher.data.entity.DBCat
import kotlinx.coroutines.flow.Flow

@Dao
interface DBCatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DBCat)
    
    @Delete
    suspend fun delete(cat: DBCat)

    @Query("SELECT * FROM cat")
    suspend fun getAll(): List<DBCat>

    @Query("SELECT * FROM cat")
    fun observeAll(): Flow<List<DBCat>>
}