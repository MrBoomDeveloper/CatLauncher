package com.mrboomdev.catlauncher.data.entity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import androidx.compose.ui.graphics.painter.Painter
import com.google.accompanist.drawablepainter.DrawablePainter

data class App(
    val packageName: String,
    val activityName: String,
    val version: String = "Unknown",
    val title: String,
    val ogTitle: String = title,
    val icon: Painter,
    val cats: List<Int>
)

fun ResolveInfo.toApp(
    context: Context
): App {
    val packageInfo = context.packageManager.getPackageInfo(
        activityInfo.applicationInfo.packageName, 
        0
    )
    
    return App(
        packageName = activityInfo.applicationInfo.packageName,
        activityName = activityInfo.name,
        version = "${packageInfo.versionName} (${packageInfo.longVersionCode})",
        title = loadLabel(context.packageManager).toString(),
        icon = DrawablePainter(loadIcon(context.packageManager)),
        cats = packageInfo.applicationInfo!!.category.let { category ->
            when(category) {
                ApplicationInfo.CATEGORY_GAME -> DBCat.SYSTEM_GAMES
                ApplicationInfo.CATEGORY_MAPS -> DBCat.SYSTEM_MAPS
                ApplicationInfo.CATEGORY_NEWS -> DBCat.SYSTEM_NEWS
                ApplicationInfo.CATEGORY_AUDIO -> DBCat.SYSTEM_AUDIO
                ApplicationInfo.CATEGORY_ACCESSIBILITY -> DBCat.SYSTEM_ACCESSIBILITY
                ApplicationInfo.CATEGORY_IMAGE -> DBCat.SYSTEM_IMAGES
                ApplicationInfo.CATEGORY_PRODUCTIVITY -> DBCat.SYSTEM_PRODUCTIVITY
                ApplicationInfo.CATEGORY_SOCIAL -> DBCat.SYSTEM_SOCIAL
                ApplicationInfo.CATEGORY_VIDEO -> DBCat.SYSTEM_VIDEOS
                else -> null
            }?.also { 
                return@let listOf(it)
            }
            
            emptyList()
        }
    )
}

val App.intent: Intent
    get() = Intent(Intent.ACTION_MAIN).apply {
        setComponent(ComponentName(
            packageName,
            activityName
        ))
        
        addCategory(Intent.CATEGORY_LAUNCHER)
        
        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
    }