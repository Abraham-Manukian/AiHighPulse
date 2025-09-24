package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.PlaceholderScreen
import com.example.aihighpulse.ui.vm.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import org.koin.androidx.compose.get

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
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .then(Modifier)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.settings_age, p.age))
                Text(stringResource(R.string.settings_height_weight, p.heightCm, p.weightKg))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { /* edit profile */ }) { Text(stringResource(R.string.settings_edit_profile)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { vm.save(p.copy(weightKg = (p.weightKg + 0.5))) }) {
                Text(stringResource(R.string.settings_inc_weight))
            }
            OutlinedButton(onClick = { vm.save(p.copy(weightKg = (p.weightKg - 0.5))) }) {
                Text(stringResource(R.string.settings_dec_weight))
            }
        }
        Text(stringResource(R.string.settings_units_title), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = { prefs.setUnits("metric") }) { Text(stringResource(R.string.settings_units_metric)) }
            TextButton(onClick = { prefs.setUnits("imperial") }) { Text(stringResource(R.string.settings_units_imperial)) }
        }
        Text(stringResource(R.string.settings_language_title), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ru"))
                (context as? Activity)?.recreate()
                prefs.setLanguageTag("ru")
            }) { Text(stringResource(R.string.settings_language_ru)) }
            TextButton(onClick = {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                (context as? Activity)?.recreate()
                prefs.setLanguageTag(null)
            }) { Text(stringResource(R.string.settings_language_system)) }
        }
        Text(stringResource(R.string.settings_theme_title), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); prefs.setTheme("light"); (context as? Activity)?.recreate() }) { Text(stringResource(R.string.settings_theme_light)) }
            TextButton(onClick = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); prefs.setTheme("dark"); (context as? Activity)?.recreate() }) { Text(stringResource(R.string.settings_theme_dark)) }
            TextButton(onClick = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM); prefs.setTheme("system"); (context as? Activity)?.recreate() }) { Text(stringResource(R.string.settings_theme_system)) }
        }
        if (s.saving) {
            Text(stringResource(R.string.settings_saving), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
