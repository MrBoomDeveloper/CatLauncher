package com.mrboomdev.catlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.idapgroup.snowfall.snowfall
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val catApp = currentCatLauncher()
    val isLoading by catApp.isLoading.collectAsState()
    val catsWithApps by catApp.catsWithApps.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    val searchFocusRequester = remember { FocusRequester() }
    val searchInteractionSource = remember { MutableInteractionSource() }
    
    LaunchedEffect(pagerState.currentPage) {
        if(pagerState.currentPage == 0) {
            searchQuery = ""
        }
    }
    
    LaunchedEffect(Unit) { 
        searchInteractionSource.interactions.collect { interaction ->
            if(interaction is PressInteraction.Release) {
                coroutineScope.launch { 
                    pagerState.animateScrollToPage(1)
                }
            }
        }
    }

    BackHandler(enabled = pagerState.currentPage != 0) { 
        coroutineScope.launch { 
            pagerState.animateScrollToPage(0)
        }
    }
    
    CatLauncherTheme {
        if(isLoading) {
            Dialog(
                onDismissRequest = {}
            ) {
                CircularProgressIndicator()
            }
        }

        Box {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .snowfall()
            )
            
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
                                searchFocusRequester.requestFocus()
                                keyboardController?.show()
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
                            
                            val queryFontFamily = remember {
                                FontFamily(
                                    Font(
                                        resId = R.font.google_sans_flex,
                                        variationSettings = FontVariation.Settings(
                                            FontVariation.weight(500)
                                        )
                                    )
                                )
                            }

                            BasicTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(searchFocusRequester),
                                
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                interactionSource = searchInteractionSource,
                                
                                textStyle = LocalTextStyle.current.copy(
                                    fontFamily = queryFontFamily,
                                    color = LocalContentColor.current
                                ),
                                
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                
                                decorationBox = {
                                    it()
                                    
                                    if(searchQuery.isNotEmpty()) {
                                        return@BasicTextField
                                    }
                                    
                                    Text(
                                        text = "Search",
                                        fontFamily = queryFontFamily
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
                                
                                catsWithApps = catsWithApps.map { (cat, apps) ->
                                    cat to apps.filter { app -> 
                                        app.title.contains(searchQuery)
                                    }
                                }
                            )
                        }

                        else -> throw IllegalStateException("Illegal page $index!")
                    }
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