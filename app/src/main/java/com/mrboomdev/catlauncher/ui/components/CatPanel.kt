package com.mrboomdev.catlauncher.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.mrboomdev.catlauncher.R
import com.mrboomdev.catlauncher.data.entity.App
import com.mrboomdev.catlauncher.data.entity.Cat
import com.mrboomdev.catlauncher.ui.dialogs.CustomizeAppDialog

@OptIn(ExperimentalTextApi::class)
@Composable
fun CatPanel(
    modifier: Modifier = Modifier,
    cat: Cat,
    apps: List<App>
) {
    val context = LocalContext.current
    var selectedApp by remember { mutableStateOf<App?>(null) }

    selectedApp?.also { app ->
        CustomizeAppDialog(
            onDismissRequest = { selectedApp = null },
            app = app
        )
    }
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x99000000))
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            cat.icon?.also { icon ->
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            Text(
                modifier = Modifier.weight(1f),
                text = cat.name,
                color = Color.White,
                
                fontFamily = remember {
                    FontFamily(
                        Font(
                            resId = R.font.google_sans_flex,
                            variationSettings = FontVariation.Settings(
                                FontVariation.width(115f),
                                FontVariation.weight(600),
                                FontVariation.opticalSizing(12.sp)
                            )
                        )
                    )
                }
            )

            Box {
                var showOptions by remember { mutableStateOf(false) }

                CompositionLocalProvider(
                    LocalContentColor provides Color.White
                ) {
                    IconButton({ showOptions = true }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_more_vert),
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = showOptions,
                    onDismissRequest = { showOptions = false },
                    shape = RoundedCornerShape(32.dp)
                ) {
                    DropdownMenuItem(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 32.dp
                        ),

                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_reorder),
                                contentDescription = null
                            )
                        },

                        text = {
                            Text(
                                text = "Reorder"
                            )
                        },

                        onClick = {

                        }
                    )
                    
                    DropdownMenuItem(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 32.dp
                        ),

                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_image_outlined),
                                contentDescription = null
                            )
                        },
                        
                        text = {
                            Text(
                                text = "Change icon"
                            )
                        },

                        onClick = {

                        }
                    )

                    DropdownMenuItem(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 32.dp
                        ),
                        
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_label_outlined),
                                contentDescription = null
                            )
                        },
                        
                        text = {
                            Text(
                                text = "Rename"
                            )
                        },

                        onClick = {

                        }
                    )

                    DropdownMenuItem(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 32.dp
                        ),
                        
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_delete_outlined),
                                contentDescription = null
                            )
                        },
                        
                        text = {
                            Text(
                                text = "Delete"
                            )
                        },

                        onClick = {

                        }
                    )
                }
            }
        }

        VerticalGrid(
            columns = SimpleGridCells.Fixed(5),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for(app in apps) {
                AppIcon(
                    app = app,
                    
                    onClick = {
                        context.startActivity(app.intent)
                    },
                    
                    onLongClick = {
                        selectedApp = app
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatPanelPreview() {
    val icon = painterResource(R.drawable.bocchi)
    
    val cat = remember { 
        Cat(
            id = 0,
            name = "Chat"
        )
    }

    val apps = remember {
        List(15) { i ->
            App(
                title = "App $i",
                icon = icon,
                intent = Intent(),
                cats = emptyList()
            )
        }
    }
    
    CatPanel(
        cat = cat,
        apps = apps
    )
}