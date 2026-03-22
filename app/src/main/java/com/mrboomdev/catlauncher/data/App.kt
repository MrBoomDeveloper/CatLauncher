package com.mrboomdev.catlauncher.data

import android.R.attr.name
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri

data class App(
    val title: String,
    val icon: Drawable,
    val intent: Intent,
    val cats: List<Int>
)

fun ResolveInfo.toApp(
    context: Context,
    cats: List<Int>
) = App(
    title = loadLabel(context.packageManager).toString(),
    icon = loadIcon(context.packageManager),
    intent = activityInfo.intent,
    cats = cats
)

private val ActivityInfo.intent: Intent
    get() = Intent(Intent.ACTION_MAIN).apply {
        setComponent(ComponentName(
            applicationInfo.packageName,
            name
        ))
        
        addCategory(Intent.CATEGORY_LAUNCHER)
        
        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
    }