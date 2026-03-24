package com.mrboomdev.catlauncher.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "cat")
data class DBCat(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val icon: Int? = null
) {
    companion object {
        const val SYSTEM_ACCESSIBILITY = -1
        const val SYSTEM_AUDIO = -2
        const val SYSTEM_GAMES = -3
        const val SYSTEM_IMAGES = -4
        const val SYSTEM_MAPS = -5
        const val SYSTEM_NEWS = -6
        const val SYSTEM_PRODUCTIVITY = -7
        const val SYSTEM_SOCIAL = -8
        const val SYSTEM_VIDEOS = -9
    }
}