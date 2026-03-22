package com.mrboomdev.catlauncher.data.db.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.mrboomdev.catlauncher.data.db.entity.DBAppCustomization

@Dao
interface DBAppCustomizationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: DBAppCustomization)
    
    @Query("SELECT * FROM app_customization")
    fun getAll(): List<DBAppCustomization>
}