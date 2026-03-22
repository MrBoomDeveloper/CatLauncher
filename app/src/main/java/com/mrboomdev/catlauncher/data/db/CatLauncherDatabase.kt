package com.mrboomdev.catlauncher.data.db

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.mrboomdev.catlauncher.data.db.entity.DBAppCustomization

@Database(
    version = 1,
    entities = [
        DBAppCustomization::class
    ]
)
abstract class CatLauncherDatabase: RoomDatabase() {
    abstract val appCustomization: DBAppCustomization
}