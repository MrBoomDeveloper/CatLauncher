package com.mrboomdev.catlauncher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrboomdev.catlauncher.R
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


@OptIn(ExperimentalTextApi::class)
@Composable
fun HomeScreen() {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { 
        while(true) {
            val calendar = Calendar.getInstance()
            val date = LocalDate.now()
            
            val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
            currentDate = date.format(dateFormatter)

            fun formatTimePart(timePart: Int) = if(timePart >= 10) {
                timePart.toString()
            } else "0${timePart}"
            
            currentTime = formatTimePart(
                calendar.get(Calendar.HOUR_OF_DAY)
            ) + "\n" + formatTimePart(
                calendar.get(Calendar.MINUTE)
            )

            delay(60_000)
        }
    }
    
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primaryFixed,
                    textAlign = TextAlign.Center,
                    text = currentDate,

                    fontFamily = remember {
                        FontFamily(
                            Font(
                                resId = R.font.google_sans_flex,
                                variationSettings = FontVariation.Settings(
                                    FontVariation.width(115f),
                                    FontVariation.weight(800),
                                    FontVariation.opticalSizing(16.sp)
                                )
                            )
                        )
                    },
                )
                
                Text(
                    fontSize = 112.sp,
                    lineHeight = 112.sp,
                    color = MaterialTheme.colorScheme.primaryFixed,
                    textAlign = TextAlign.Center,
                    text = currentTime,

                    fontFamily = remember {
                        FontFamily(
                            Font(
                                resId = R.font.google_sans_flex,
                                variationSettings = FontVariation.Settings(
                                    FontVariation.width(115f),
                                    FontVariation.weight(800),
                                    FontVariation.opticalSizing(112.sp)
                                )
                            )
                        )
                    },
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    backgroundColor = 0xff000000
)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}