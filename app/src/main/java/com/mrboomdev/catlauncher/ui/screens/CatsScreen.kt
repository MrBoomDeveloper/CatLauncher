package com.mrboomdev.catlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrboomdev.catlauncher.R
import com.mrboomdev.catlauncher.currentCatLauncher
import com.mrboomdev.catlauncher.data.entity.App
import com.mrboomdev.catlauncher.data.entity.DBCat
import com.mrboomdev.catlauncher.ui.components.CatPanel

@Composable
fun CatsScreen(
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues,
    catsWithApps: List<Pair<DBCat, List<App>>>
) {
    val catLauncher = currentCatLauncher()
    val uncategorizedApps by catLauncher.appsWithoutCats.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .background(Color(0x99000000))
            .fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        overscrollEffect = null,
        contentPadding = contentPadding
    ) {
        items(
            key = { it.first.id },
            items = catsWithApps
        ) { (cat, apps) ->
            CatPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
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
            DBCat(
                id = listIndex,
                name = "Cat $listIndex"
            ) to List(15) { appIndex ->
                App(
                    packageName = "",
                    activityName = "",
                    title = "App $appIndex",
                    icon = icon,
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