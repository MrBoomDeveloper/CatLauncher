package com.mrboomdev.catlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

@OptIn(ExperimentalTextApi::class)
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
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                        )).padding(16.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .25f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = {
                        coroutineScope.launch { 
                            pagerState.animateScrollToPage(1)
                        }
                    }
                ) { 
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null
                        )
                        
                        Text(
                            text = "Search",
                            fontFamily = remember {
                                FontFamily(
                                    Font(
                                        resId = R.font.google_sans_flex,
                                        variationSettings = FontVariation.Settings(
                                            FontVariation.weight(500)
                                        )
                                    )
                                )
                            }
                        )
                    }
                }
            }
        ) { contentPadding -> 
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
                    0 -> HomeScreen(contentPadding)
                    1 -> CompositionLocalProvider(LocalOverscrollFactory provides null) {
                        CatsScreen(
                            contentPadding = WindowInsets(
                                left = contentPadding.calculateLeftPadding(LocalLayoutDirection.current),
                                top = contentPadding.calculateTopPadding(),
                                right = contentPadding.calculateRightPadding(LocalLayoutDirection.current),
                                bottom = contentPadding.calculateBottomPadding()
                            ).add(WindowInsets(4.dp, 4.dp, 4.dp, 4.dp)).asPaddingValues(),
                            catsWithApps = catsWithApps
                        )
                    }

                    else -> throw IllegalStateException("Illegal page $index!")
                }
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