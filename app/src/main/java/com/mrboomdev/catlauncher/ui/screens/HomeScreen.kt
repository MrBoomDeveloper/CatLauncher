package com.mrboomdev.catlauncher.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalTextApi::class)
@Composable
fun HomeScreen(contentPadding: PaddingValues) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { 
        while(true) {
            val calendar = Calendar.getInstance()

            @SuppressLint("SimpleDateFormat")
            currentDate = SimpleDateFormat("EEE, MMM d").format(Date())

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
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_MAIN).apply { 
                            addCategory(Intent.CATEGORY_APP_CALENDAR)
                        })
                    }
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
                        }
                    )
                }
                
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS))
                    }
                ) {
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
                        }
                    )
                }
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
    HomeScreen(PaddingValues())
}