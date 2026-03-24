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
import com.mrboomdev.catlauncher.data.entity.DBCat
import com.mrboomdev.catlauncher.data.entity.toApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class CatLauncher(private val context: Context) {
    val database = Room.databaseBuilder(
        context = context,
        klass = CatLauncherDatabase::class.java,
        name = "settings"
    ).build()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    private val _cats = MutableStateFlow(emptyList<DBCat>())
    val cats = _cats.asStateFlow()

    private val _installedApps = MutableStateFlow(emptyList<App>())
    val installedApps = _installedApps.asStateFlow()

    val customApps = installedApps.combine(
        database.appCustomization.observeAll()
    ) { installed, customizations ->
        val customMap = customizations.associateBy {
            it.packageName to it.activityName
        }

        installed.filter { app ->
            if(app.packageName == context.packageName) {
                // Hide own launcher shortcut
                return@filter false
            }
            
            val custom = customMap[app.packageName to app.activityName]
            custom?.isHidden != true
        }.map { app ->
            val custom = customMap[app.packageName to app.activityName]
            
            app.copy(
                title = custom?.customTitle ?: app.title,
                ogTitle = app.title
            )
        }.sortedBy { app ->
            app.title
        }
    }.stateIn(
        scope = GlobalScope, 
        started = SharingStarted.WhileSubscribed(), 
        initialValue = emptyList()
    )
    
    val catsWithApps = combine(
        cats,
        customApps
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
            DBCat(
                id = 0,
                name = "Accessibility",
                icon = R.drawable.ic_accessibility
            ),

            DBCat(
                id = 1,
                name = "Audio",
                icon = R.drawable.ic_headphones_outlined
            ),

            DBCat(
                id = 2,
                name = "Games",
                icon = R.drawable.ic_videogame_asset_outlined
            ),

            DBCat(
                id = 3,
                name = "Images",
                icon = R.drawable.ic_image_outlined
            ),

            DBCat(
                id = 4,
                name = "Maps",
                icon = R.drawable.ic_location_on_outlined
            ),

            DBCat(
                id = 5,
                name = "News",
                icon = R.drawable.ic_newspaper_outlined
            ),

            DBCat(
                id = 6,
                name = "Productivity",
                icon = R.drawable.ic_checklist
            ),

            DBCat(
                id = 7,
                name = "Social",
                icon = R.drawable.ic_chat_outlined
            ),
            
            DBCat(
                id = 8,
                name = "Videos",
                icon = R.drawable.ic_movie_outlined
            )
        ))
        
        _installedApps.emit(context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }, PackageManager.MATCH_ALL).map { resolveInfo ->
            resolveInfo.toApp(
                context = context,
                cats = _cats.value.map { it.id }
            )
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