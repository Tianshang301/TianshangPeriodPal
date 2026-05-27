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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tianshang.periodpal.R
import com.tianshang.periodpal.ui.theme.HslColorUtils
import com.tianshang.periodpal.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(context)
    )
    
    val settings by viewModel.settings.collectAsState()
    
    // Parse current HSL from stored hex
    val currentHsl = remember(settings.themeColor) {
        HslColorUtils.hexToHsl(settings.themeColor)
    }
    
    var hue by remember(currentHsl) { mutableFloatStateOf(currentHsl.hue) }
    var saturation by remember(currentHsl) { mutableFloatStateOf(currentHsl.saturation) }
    var lightness by remember(currentHsl) { mutableFloatStateOf(currentHsl.lightness) }
    
    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setBackgroundImage(it) }
    }
    
    // Preset colors
    val presets = listOf(
        Triple(340f, 60f, 85f) to R.string.color_pink,
        Triple(330f, 100f, 71f) to R.string.color_dark_pink,
        Triple(330f, 100f, 54f) to R.string.color_peach,
        Triple(340f, 50f, 76f) to R.string.color_light_purple,
        Triple(325f, 78f, 59f) to R.string.color_medium_purple
    )
    
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
            // Color preview
            Text(
                stringResource(R.string.theme_color),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Live preview circle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(HslColorUtils.hslToColor(hue, saturation, lightness))
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
            
            // HSL Sliders
            Text(
                stringResource(R.string.hue),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = hue,
                onValueChange = { hue = it },
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = HslColorUtils.hslToColor(hue, saturation, lightness),
                    activeTrackColor = HslColorUtils.hslToColor(hue, saturation, lightness)
                )
            )
            Text(
                text = "${hue.toInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                stringResource(R.string.saturation),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = saturation,
                onValueChange = { saturation = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = HslColorUtils.hslToColor(hue, saturation, lightness),
                    activeTrackColor = HslColorUtils.hslToColor(hue, saturation, lightness)
                )
            )
            Text(
                text = "${saturation.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                stringResource(R.string.lightness),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = lightness,
                onValueChange = { lightness = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = HslColorUtils.hslToColor(hue, saturation, lightness),
                    activeTrackColor = HslColorUtils.hslToColor(hue, saturation, lightness)
                )
            )
            Text(
                text = "${lightness.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
            
            // Apply button
            Button(
                onClick = {
                    val hex = HslColorUtils.hslToHex(hue, saturation, lightness)
                    viewModel.setThemeColor(hex)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.apply_color))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Preset colors as quick shortcuts
            Text(
                stringResource(R.string.preset_colors),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                presets.forEach { (hsl, nameResId) ->
                    val presetColor = HslColorUtils.hslToColor(hsl.first, hsl.second, hsl.third)
                    val hex = HslColorUtils.hslToHex(hsl.first, hsl.second, hsl.third)
                    val isSelected = settings.themeColor == hex
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            viewModel.setThemeColor(hex)
                            hue = hsl.first
                            saturation = hsl.second
                            lightness = hsl.third
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(presetColor)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    } else {
                                        Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(nameResId),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Background customization
            Text(
                stringResource(R.string.background_customization),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Background image preview and selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") }
                ) {
                    Text(stringResource(R.string.select_background_image))
                }
                
                if (settings.backgroundImageUri != null) {
                    OutlinedButton(
                        onClick = { viewModel.removeBackgroundImage() }
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
