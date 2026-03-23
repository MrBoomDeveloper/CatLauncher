package com.mrboomdev.catlauncher.ui.dialogs

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrboomdev.catlauncher.R
import com.mrboomdev.catlauncher.data.entity.App

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun CustomizeAppDialog(
    onDismissRequest: () -> Unit,
    app: App
) {
    val context = LocalContext.current
    val newTitle = rememberTextFieldState()
    
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit,
                painter = app.icon,
                contentDescription = null
            )

            val font = remember {
                FontFamily(
                    Font(
                        resId = R.font.google_sans_flex,
                        variationSettings = FontVariation.Settings(
                            FontVariation.width(115f),
                            FontVariation.weight(600),
                            FontVariation.opticalSizing(22.sp)
                        )
                    )
                )
            }

            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 1.dp),
                state = newTitle,
                lineLimits = TextFieldLineLimits.SingleLine,
                shape = RoundedCornerShape(8.dp),

                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),

                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = font
                ),

                contentPadding = OutlinedTextFieldDefaults.contentPadding(
                    top = 12.dp,
                    bottom = 12.dp
                ),

                placeholder = {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = app.title,
                        fontFamily = font
                    )
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val buttonFont = remember {
                FontFamily(
                    Font(
                        resId = R.font.google_sans_flex,
                        variationSettings = FontVariation.Settings(
                            FontVariation.width(115f),
                            FontVariation.weight(500),
                            FontVariation.opticalSizing(16.sp)
                        )
                    )
                )
            }
            
            for(action in arrayOf(
                Triple(
                    R.drawable.ic_settings_outlined,
                    "Settings"
                ) {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", app.intent.component!!.packageName, null)))
                },

                Triple(
                    R.drawable.ic_share_outlined,
                    "Share"
                ) {

                },

                Triple(
                    R.drawable.ic_delete_outlined,
                    "Uninstall"
                ) {
                    context.startActivity(Intent(Intent.ACTION_DELETE, Uri.fromParts("package", app.intent.component!!.packageName, null)))
                },

                Triple(
                    R.drawable.ic_block,
                    "Hide"
                ) {

                }
            )) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),
                    onClick = action.third
                ) { 
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(action.first),
                            contentDescription = null,
                        )
                        
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = buttonFont,
                            text = action.second
                        )
                    }
                }
            }
        }
    }
}