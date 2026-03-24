package com.mrboomdev.catlauncher.data.db

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.mrboomdev.catlauncher.data.db.dao.DBAppCustomizationDao
import com.mrboomdev.catlauncher.data.db.dao.DBCatDao
import com.mrboomdev.catlauncher.data.entity.DBAppCustomization
import com.mrboomdev.catlauncher.data.entity.DBCat

@Database(
    version = 1,
    entities = [
        DBAppCustomization::class,
        DBCat::class
    ]
)
abstract class CatLauncherDatabase: RoomDatabase() {
    abstract val appCustomization: DBAppCustomizationDao
    abstract val cat: DBCatDao
}