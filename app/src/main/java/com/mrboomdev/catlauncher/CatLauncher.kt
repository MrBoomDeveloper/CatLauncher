package com.mrboomdev.catlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import com.mrboomdev.catlauncher.data.App
import com.mrboomdev.catlauncher.data.Cat
import com.mrboomdev.catlauncher.data.toApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class CatLauncher(private val context: Context) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    private val _cats = MutableStateFlow(emptyList<Cat>())
    val cats = _cats.asStateFlow()

    private val _apps = MutableStateFlow(emptyList<App>())
    val apps = _apps.asStateFlow()
    
    val catsWithApps = combine(
        cats,
        apps
    ) { currentCats, currentApps ->
        currentCats.map { cat ->
            val matchingApps = currentApps.filter { app ->
                cat.id in app.cats
            }
            
            Pair(cat, matchingApps)
        }
    }.stateIn(
        scope = GlobalScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
    
    suspend fun init() {
        _cats.emit(listOf(
            Cat(
                id = 0,
                name = "Chat"
            ),

            Cat(
                id = 1,
                name = "Social"
            ),

            Cat(
                id = 2,
                name = "Food"
            ),

            Cat(
                id = 3,
                name = "Money"
            )
        ))
        
        _apps.emit(context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }, PackageManager.MATCH_ALL).map { resolveInfo ->
            resolveInfo.toApp(
                context = context,
                cats = _cats.value.map { it.id }
            )
        }.sortedBy { app ->
            app.title
        })
        
        _isLoading.emit(false)
    }
}

@Composable
fun currentCatLauncher(): CatLauncher {
    val app = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    
    return if(app is CatApplication) {
        app.catLauncher
    } else retain {
        CatLauncher(app).apply { 
            coroutineScope.launch(Dispatchers.IO) {
                init()
            }
        }
    }
}