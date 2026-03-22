package com.mrboomdev.catlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mrboomdev.catlauncher.data.entity.Cat
import com.mrboomdev.catlauncher.ui.screens.CatsScreen
import com.mrboomdev.catlauncher.ui.screens.HomeScreen
import com.mrboomdev.catlauncher.ui.theme.CatLauncherTheme
import kotlinx.coroutines.launch

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
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    
    CatLauncherTheme {
        if(isLoading) {
            Dialog(
                onDismissRequest = {}
            ) {
                CircularProgressIndicator()
            }
                
            return@CatLauncherTheme
        }

        VerticalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            pageNestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                        // currentPageOffsetFraction ranges from -0.5 to 0.5
                        // If we are on page 1 (index 1), an offset of -0.1 means 
                        // page 0 is visible by 10%.
                        if(pagerState.currentPage == 1 && pagerState.currentPageOffsetFraction <= -0.1f) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                        
                        return super.onPostFling(consumed, available)
                    }
                }
            }
        ) { index ->
            when(index) {
                0 -> HomeScreen()
                1 -> CompositionLocalProvider(LocalOverscrollFactory provides null) {
                    CatsScreen(
                        contentPadding = WindowInsets.safeDrawing
                            .add(WindowInsets(4.dp, 4.dp, 4.dp, 4.dp))
                            .asPaddingValues(),
                        catsWithApps = catsWithApps
                    )
                }
                    
                else -> throw IllegalStateException("Illegal page $index!")
            }
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = painterResource(R.drawable.wallpaper),
            contentDescription = null
        )
        
        App()
    }
}