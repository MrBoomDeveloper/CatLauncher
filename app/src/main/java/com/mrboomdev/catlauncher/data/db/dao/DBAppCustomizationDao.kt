package com.mrboomdev.catlauncher.data.db.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.mrboomdev.catlauncher.data.entity.DBAppCustomization
import kotlinx.coroutines.flow.Flow

@Dao
interface DBAppCustomizationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DBAppCustomization)
    
    @Query("SELECT * FROM app_customization")
    suspend fun getAll(): List<DBAppCustomization>

    @Query("SELECT * FROM app_customization")
    fun observeAll(): Flow<List<DBAppCustomization>>
}