package com.mrboomdev.catlauncher.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import com.mrboomdev.catlauncher.R

object GoogleSansFlex {
    @OptIn(ExperimentalTextApi::class)
    val regular = FontFamily(
        Font(
            resId = R.font.google_sans_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(500),
            )
        )
    )
}