package com.mrboomdev.catlauncher.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrboomdev.catlauncher.R
import com.mrboomdev.catlauncher.data.entity.App
import com.mrboomdev.catlauncher.data.entity.Cat
import com.mrboomdev.catlauncher.ui.components.CatPanel

@Composable
fun CatsScreen(
    contentPadding: PaddingValues,
    catsWithApps: List<Pair<Cat, List<App>>>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        overscrollEffect = null,
        contentPadding = contentPadding
    ) { 
        items(
            key = { it.first.id },
            items = catsWithApps
        ) { (cat, apps) ->
            CatPanel(
                modifier = Modifier.animateItem(),
                cat = cat,
                apps = apps
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatsScreenPreview() {
    val icon = painterResource(R.drawable.bocchi)
    
    val catsWithApps = remember {
        List(5) { listIndex ->
            Cat(
                id = listIndex,
                name = "Cat $listIndex"
            ) to List(15) { appIndex ->
                App(
                    title = "App $appIndex",
                    icon = icon,
                    intent = Intent(),
                    cats = emptyList()
                )
            }
        }
    }
    
    CatsScreen(
        contentPadding = PaddingValues(8.dp),
        catsWithApps = catsWithApps
    )
}