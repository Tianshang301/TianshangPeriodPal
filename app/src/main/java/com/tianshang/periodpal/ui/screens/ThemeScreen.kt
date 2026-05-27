package com.tianshang.periodpal.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tianshang.periodpal.R
import com.tianshang.periodpal.ui.components.ColorPickerDialog
import com.tianshang.periodpal.ui.theme.ExtraLargeShape
import com.tianshang.periodpal.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(context)
    )

    val settings by viewModel.settings.collectAsState()

    var showColorPicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setBackgroundImage(it) }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = settings.themeColor,
            onColorSelected = { viewModel.setThemeColor(it) },
            onDismiss = { showColorPicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.theme_customization)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Current color preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(settings.themeColor))
                                } catch (_: Exception) {
                                    Color(0xFFFFB6C1)
                                }
                            )
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.theme_color),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = settings.themeColor,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preset colors section
            Text(
                stringResource(R.string.color_presets),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val presets = listOf(
                "#FFB6C1" to R.string.color_pink,
                "#FF69B4" to R.string.color_dark_pink,
                "#FF1493" to R.string.color_peach,
                "#DB7093" to R.string.color_light_purple,
                "#C71585" to R.string.color_medium_purple
            )

            // Display presets in a 2-column grid
            presets.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { (colorHex, nameResId) ->
                        val isSelected = settings.themeColor == colorHex
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setThemeColor(colorHex) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected)
                                androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            else null,
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 4.dp else 1.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            try {
                                                Color(android.graphics.Color.parseColor(colorHex))
                                            } catch (_: Exception) {
                                                Color(0xFFFFB6C1)
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(nameResId),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    // Fill empty slot if odd number
                    if (row.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Custom color button
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = { showColorPicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = ExtraLargeShape
            ) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.custom_color))
            }

            Divider(modifier = Modifier.padding(vertical = 20.dp))

            // Background customization
            Text(
                stringResource(R.string.background_customization),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (settings.backgroundImageUri != null) {
                        AsyncImage(
                            model = settings.backgroundImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(
                                        alpha = 1f - (settings.backgroundTransparency / 100f)
                                    )
                                )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = ExtraLargeShape
                ) {
                    Text(stringResource(R.string.select_background_image))
                }

                if (settings.backgroundImageUri != null) {
                    OutlinedButton(
                        onClick = { viewModel.removeBackgroundImage() },
                        modifier = Modifier.weight(1f),
                        shape = ExtraLargeShape
                    ) {
                        Text(stringResource(R.string.remove_background))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Background transparency slider
            Text(
                stringResource(R.string.background_transparency),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${settings.backgroundTransparency}%",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(48.dp)
                )
                Slider(
                    value = settings.backgroundTransparency.toFloat(),
                    onValueChange = { viewModel.setBackgroundTransparency(it.toInt()) },
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
