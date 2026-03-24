package com.mrboomdev.catlauncher.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "cat")
data class DBCat(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val icon: Int? = null
)