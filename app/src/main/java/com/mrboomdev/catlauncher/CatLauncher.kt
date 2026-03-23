package com.mrboomdev.catlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import androidx.room3.Room
import com.mrboomdev.catlauncher.data.db.CatLauncherDatabase
import com.mrboomdev.catlauncher.data.entity.App
import com.mrboomdev.catlauncher.data.entity.Cat
import com.mrboomdev.catlauncher.data.entity.toApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
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
    
    val database = Room.databaseBuilder(
        context = context,
        klass = CatLauncherDatabase::class.java,
        name = "settings"
    )
    
    suspend fun init() {
        _cats.emit(listOf(
            Cat(
                id = 0,
                name = "Accessibility",
                icon = R.drawable.ic_accessibility
            ),

            Cat(
                id = 1,
                name = "Audio",
                icon = R.drawable.ic_headphones_outlined
            ),

            Cat(
                id = 2,
                name = "Games",
                icon = R.drawable.ic_videogame_asset_outlined
            ),

            Cat(
                id = 3,
                name = "Images",
                icon = R.drawable.ic_image_outlined
            ),

            Cat(
                id = 4,
                name = "Maps",
                icon = R.drawable.ic_location_on_outlined
            ),

            Cat(
                id = 5,
                name = "News",
                icon = R.drawable.ic_newspaper_outlined
            ),

            Cat(
                id = 6,
                name = "Productivity",
                icon = R.drawable.ic_checklist
            ),

            Cat(
                id = 7,
                name = "Social",
                icon = R.drawable.ic_chat_outlined
            ),
            
            Cat(
                id = 8,
                name = "Videos",
                icon = R.drawable.ic_movie_outlined
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