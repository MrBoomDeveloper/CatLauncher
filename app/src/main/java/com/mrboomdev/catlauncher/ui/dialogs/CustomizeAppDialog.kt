package com.mrboomdev.catlauncher.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.FileProvider
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.m3.OutlinedChipTextField
import com.dokar.chiptextfield.rememberChipTextFieldState
import com.mrboomdev.catlauncher.R
import com.mrboomdev.catlauncher.currentCatLauncher
import com.mrboomdev.catlauncher.data.entity.App
import com.mrboomdev.catlauncher.data.entity.DBAppCustomization
import com.mrboomdev.catlauncher.ui.theme.GoogleSansFlex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomizeAppDialog(
    onDismissRequest: () -> Unit,
    app: App
) {
    val context = LocalContext.current
    val catLauncher = currentCatLauncher()
    val coroutineScope = rememberCoroutineScope()
    val selectedCatsState = rememberChipTextFieldState<Chip>()
    val newTitle = rememberTextFieldState(if(app.title != app.ogTitle) app.title else "")
    var isLoading by remember { mutableStateOf(false) }
    var newCat by remember { mutableStateOf("") }

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
    
    if(isLoading) {
        Dialog({}) {
            LoadingIndicator()
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = {
            val newTitle = newTitle.text.toString()
            
            if(newTitle.isBlank() && app.title != app.ogTitle) {
                coroutineScope.launch(Dispatchers.IO) {
                    catLauncher.database.appCustomization.insert(
                        DBAppCustomization(
                            packageName = app.packageName,
                            activityName = app.activityName,
                            isHidden = false,
                            customTitle = null
                        )
                    )

                    onDismissRequest()
                }

                return@ModalBottomSheet
            } else if(newTitle.isNotBlank() && newTitle != app.title) {
                coroutineScope.launch(Dispatchers.IO) {
                    catLauncher.database.appCustomization.insert(
                        DBAppCustomization(
                            packageName = app.packageName,
                            activityName = app.activityName,
                            isHidden = false,
                            customTitle = newTitle
                        )
                    )

                    onDismissRequest()
                }

                return@ModalBottomSheet
            }
            
            onDismissRequest()
        }
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

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            text = "Categories",
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
            OutlinedChipTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                state = selectedCatsState,
                value = newCat,

                onValueChange = {
                    newCat = it
                },

                onSubmit = {
                    Chip(it)
                }
            )
            
            val matchingCats by remember(newCat) {
                catLauncher.cats.map { cats ->
                    cats.filter { cat ->
                        cat.name.contains(newCat, ignoreCase = true) && selectedCatsState.chips.none { chip ->
                            chip.text == cat.name
                        }
                    }.sortedBy { cat ->
                        cat.name
                    }
                }
            }.collectAsState(emptyList())

            DropdownMenu(
                expanded = newCat.isNotBlank() && matchingCats.isNotEmpty(),
                
                properties = PopupProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    focusable = false
                ),
                
                onDismissRequest = {
                    
                }
            ) {
                matchingCats.forEach { cat ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = cat.name
                            )
                        },

                        onClick = {
                            newCat = ""
                            selectedCatsState.addChip(Chip(cat.name))
                        }
                    )
                }
            }
        }
        
        SelectionContainer(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = GoogleSansFlex.regular,
                text = buildAnnotatedString {
                    append("Class: ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(app.packageName)
                        append("/")
                        
                        if(app.activityName.startsWith(app.packageName)) {
                            append(app.activityName.substringAfter(app.packageName))
                        } else {
                            append(app.activityName)
                        }
                    }

                    append("\nVersion: ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(app.version)
                    }
                }
            )
        }

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
            
            for(action in arrayOf<Triple<Int, String, () -> Unit>>(
                Triple(
                    R.drawable.ic_settings_outlined,
                    "Settings"
                ) {
                    context.startActivity(Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, 
                        Uri.fromParts("package", app.packageName, null)
                    ))
                },

                Triple(
                    R.drawable.ic_share_outlined,
                    "Share"
                ) {
                    shareApp(context, app.packageName)
                },

                Triple(
                    R.drawable.ic_delete_outlined,
                    "Uninstall"
                ) {
                    context.startActivity(Intent(
                        Intent.ACTION_DELETE, 
                        Uri.fromParts("package", app.packageName, null)
                    ))
                },

                Triple(
                    R.drawable.ic_block,
                    "Hide"
                ) {
                    isLoading = true
                    
                    coroutineScope.launch(Dispatchers.IO) {
                        catLauncher.database.appCustomization.insert(
                            DBAppCustomization(
                                packageName = app.packageName,
                                activityName = app.activityName,
                                isHidden = true,
                                customTitle = null
                            )
                        )
                        
                        onDismissRequest()
                    }
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

private fun shareApp(context: Context, packageName: String) {
    val pm = context.packageManager
    val appInfo = pm.getApplicationInfo(packageName, 0)
    val splits = appInfo.splitSourceDirs
    
    val outputFile: File
    val mimeType: String

    if(!splits.isNullOrEmpty()) {
        outputFile = File(context.cacheDir, "${packageName}.apks")
        val allPaths = listOf(appInfo.sourceDir) + splits
        zipFiles(allPaths, outputFile)
        mimeType = "application/octet-stream"
    } else {
        outputFile = File(context.cacheDir, "${packageName}.apk")
        File(appInfo.sourceDir).inputStream().use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        mimeType = "application/vnd.android.package-archive"
    }
    
    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            outputFile
        ))
    }, "Share App"))
}

private fun zipFiles(srcFilePaths: List<String>, destFile: File) {
    ZipOutputStream(FileOutputStream(destFile)).use { zipOut ->
        srcFilePaths.forEach { path ->
            val file = File(path)
            FileInputStream(file).use { fis ->
                zipOut.putNextEntry(ZipEntry(file.name))
                fis.copyTo(zipOut)
                zipOut.closeEntry()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomizeAppDialogPreview() {
    val icon = painterResource(R.drawable.bocchi)
    
    val app = remember {
        App(
            packageName = "",
            activityName = "",
            title = "CatLauncher",
            icon = icon,
            cats = emptyList()
        )
    }

    CustomizeAppDialog(
        onDismissRequest = {},
        app = app
    )
}