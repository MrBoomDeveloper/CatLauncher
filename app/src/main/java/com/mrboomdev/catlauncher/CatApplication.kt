package com.mrboomdev.catlauncher

import android.app.Application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class CatApplication: Application() {
    val catLauncher by lazy {
        CatLauncher(this)
    }
    
    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch(Dispatchers.IO) {
            catLauncher.init()
        }
    }
}