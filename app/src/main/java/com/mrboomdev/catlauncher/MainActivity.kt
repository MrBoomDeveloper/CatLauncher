package com.mrboomdev.catlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mrboomdev.catlauncher.data.Cat
import com.mrboomdev.catlauncher.ui.screens.CatsScreen
import com.mrboomdev.catlauncher.ui.theme.CatLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
private fun App() {
    val catApp = currentCatLauncher()
    val isLoading by catApp.isLoading.collectAsState()
    val catsWithApps by catApp.catsWithApps.collectAsState()
    
    CatLauncherTheme {
        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.wallpaper),
                contentDescription = null
            )

            if(isLoading) {
                Dialog(
                    onDismissRequest = {}
                ) {
                    CircularProgressIndicator()
                }
                
                return@Box
            }
            
            CatsScreen(
                contentPadding = WindowInsets.safeDrawing
                    .add(WindowInsets(8.dp, 8.dp, 8.dp, 8.dp))
                    .asPaddingValues(),
                catsWithApps = catsWithApps
            )
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}