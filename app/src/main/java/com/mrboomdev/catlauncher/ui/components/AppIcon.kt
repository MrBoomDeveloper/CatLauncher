package com.mrboomdev.catlauncher.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrboomdev.catlauncher.R
import com.mrboomdev.catlauncher.data.entity.App

@OptIn(ExperimentalTextApi::class)
@Composable
fun AppIcon(
    app: App,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit,
                painter = app.icon,
                contentDescription = null
            )

            Text(
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                text = app.title,
                
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 8.sp,
                    maxFontSize = 12.sp
                ),

                fontFamily = remember {
                    FontFamily(
                        Font(
                            resId = R.font.google_sans_flex,
                            variationSettings = FontVariation.Settings(
                                FontVariation.weight(400),
                                FontVariation.opticalSizing(12.sp)
                            )
                        )
                    )
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xff000000
)
@Composable
private fun AppIconPreview() {
    val icon = painterResource(R.drawable.bocchi)
    
    val app = remember {
        App(
            title = "CatLauncher",
            icon = icon,
            intent = Intent(),
            cats = emptyList()
        )
    }
    
    AppIcon(
        app = app,
        onClick = {}
    )
}