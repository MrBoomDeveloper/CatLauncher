package com.mrboomdev.catlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.executeSQL
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
    ).apply {
        addCallback(object : RoomDatabase.Callback() {
            override suspend fun onCreate(connection: SQLiteConnection) {
                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_ACCESSIBILITY},
                        'Accessibility'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_AUDIO},
                        'Audio'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_GAMES},
                        'Games'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_IMAGES},
                        'Images'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_MAPS},
                        'Maps'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_NEWS},
                        'News'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_PRODUCTIVITY},
                        'Productivity'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_SOCIAL},
                        'Social'
                    )
                """)

                connection.executeSQL("""
                    INSERT INTO cat(
                        id,
                        name
                    ) VALUES(
                        ${DBCat.SYSTEM_VIDEOS},
                        'Videos'
                    )
                """)
            }
        }) 
    }.build()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

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

    val cats = database.cat.observeAll().stateIn(
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
        }.filter { (_, apps) ->
            // We don't want to show empty categories
            apps.isNotEmpty()
        }
    }.stateIn(
        scope = GlobalScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
    
    val appsWithoutCats = customApps.map { apps ->
        apps.filter { app ->
            app.cats.isEmpty()
        }
    }.stateIn(
        scope = GlobalScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
    
    suspend fun init() {
        _installedApps.emit(context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }, PackageManager.MATCH_ALL).map { resolveInfo ->
            resolveInfo.toApp(
                context = context
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