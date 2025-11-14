package com.example.aihighpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.PlaceholderScreen
import com.example.aihighpulse.ui.vm.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.PaddingValues
import androidx.core.os.LocaleListCompat
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import org.koin.androidx.compose.get
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.theme.AiPalette

@Composable
fun SettingsScreen() {
    val vm: SettingsViewModel = koinViewModel()
    val prefs: PreferencesRepository = get()
    val s by vm.state.collectAsState()
    val p = s.profile
    val context = LocalContext.current
    if (p == null) {
        PlaceholderScreen(
            title = stringResource(R.string.settings_no_profile_title),
            sections = listOf(stringResource(R.string.settings_no_profile_sub))
        )
        return
    }
    val contentColor = AiPalette.OnGradient
    BrandScreen(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(300))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = settingsCardColors(),
                    elevation = settingsCardElevation(),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = contentColor)
                                Text(stringResource(R.string.settings_age, p.age), color = contentColor.copy(alpha = 0.8f))
                                Text(stringResource(R.string.settings_height_weight, p.heightCm, p.weightKg), color = contentColor.copy(alpha = 0.8f))
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { /* edit profile */ },
                                modifier = Modifier.weight(1f)
                            ) { Text(stringResource(R.string.settings_edit_profile)) }
                            OutlinedButton(
                                onClick = { vm.save(p.copy(weightKg = (p.weightKg + 0.5))) },
                                modifier = Modifier.weight(1f)
                            ) { Text(stringResource(R.string.settings_inc_weight)) }
                            OutlinedButton(
                                onClick = { vm.save(p.copy(weightKg = (p.weightKg - 0.5))) },
                                modifier = Modifier.weight(1f)
                            ) { Text(stringResource(R.string.settings_dec_weight)) }
                        }
                    }
                }
            }

            PreferenceCard(title = stringResource(R.string.settings_units_title)) {
                OptionRow {
                    OptionButton(stringResource(R.string.settings_units_metric)) { prefs.setUnits("metric") }
                    OptionButton(stringResource(R.string.settings_units_imperial)) { prefs.setUnits("imperial") }
                }
            }
            PreferenceCard(title = stringResource(R.string.settings_language_title)) {
                OptionRow {
                    OptionButton(stringResource(R.string.settings_language_ru)) {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ru"))
                        (context as? Activity)?.recreate()
                        prefs.setLanguageTag("ru")
                    }
                    OptionButton(stringResource(R.string.settings_language_system)) {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                        (context as? Activity)?.recreate()
                        prefs.setLanguageTag(null)
                    }
                }
            }
            PreferenceCard(title = stringResource(R.string.settings_theme_title)) {
                OptionRow {
                    OptionButton(stringResource(R.string.settings_theme_light)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        prefs.setTheme("light")
                        (context as? Activity)?.recreate()
                    }
                    OptionButton(stringResource(R.string.settings_theme_dark)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        prefs.setTheme("dark")
                        (context as? Activity)?.recreate()
                    }
                    OptionButton(stringResource(R.string.settings_theme_system)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        prefs.setTheme("system")
                        (context as? Activity)?.recreate()
                    }
                }
            }
            if (s.saving) {
                Text(stringResource(R.string.settings_saving), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PreferenceCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = settingsCardColors(),
        elevation = settingsCardElevation(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = AiPalette.OnGradient)
            content()
        }
    }
}

@Composable
private fun OptionRow(content: @Composable RowScope.() -> Unit) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun OptionButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = AiPalette.Primary.copy(alpha = 0.18f),
            contentColor = AiPalette.DeepAccent
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun settingsCardColors() = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))

@Composable
private fun settingsCardElevation() = CardDefaults.cardElevation(defaultElevation = 6.dp)
