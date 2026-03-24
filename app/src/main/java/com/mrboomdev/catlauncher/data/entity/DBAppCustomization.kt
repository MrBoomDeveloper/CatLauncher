package com.mrboomdev.catlauncher.data.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity

@Entity(
    tableName = "app_customization",
    primaryKeys = [
        "package_name",
        "activity_name"
    ]
)
data class DBAppCustomization(
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "activity_name") val activityName: String,
    @ColumnInfo(name = "is_hidden") val isHidden: Boolean,
    @ColumnInfo(name = "custom_title") val customTitle: String?
)