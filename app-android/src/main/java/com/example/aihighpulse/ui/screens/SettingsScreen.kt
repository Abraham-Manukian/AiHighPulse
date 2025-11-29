package com.example.aihighpulse.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.example.aihighpulse.MainActivity
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.components.PlaceholderScreen
import com.example.aihighpulse.core.designsystem.theme.AiPalette
import com.example.aihighpulse.shared.domain.repository.PreferencesRepository
import com.example.aihighpulse.ui.vm.SettingsViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun SettingsScreen(onEditProfile: () -> Unit) {
    val vm: SettingsViewModel = koinViewModel()
    val prefs: PreferencesRepository = get()
    val s by vm.state.collectAsState()
    val p = s.profile
    val context = LocalContext.current
    val activity = context as? Activity

    fun restartApp() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        activity?.finish()
    }

    if (p == null) {
    LaunchedEffect(Unit) { vm.refresh() }

    BrandScreen(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        return
    }

    val contentColor = MaterialTheme.colorScheme.onSurface
        BrandScreen(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        CircleShape
                                    )
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    stringResource(R.string.settings_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                                Text(
                                    stringResource(R.string.settings_age, p.age),
                                    color = contentColor.copy(alpha = 0.8f)
                                )
                                Text(
                                    stringResource(R.string.settings_height_weight, p.heightCm, p.weightKg),
                                    color = contentColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ProfileStatsGrid(profile = p)
                        OutlinedButton(
                            onClick = onEditProfile,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) { Text(stringResource(R.string.settings_edit_profile)) }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { vm.save(p.copy(weightKg = (p.weightKg + 0.5))) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
                            ) { Text(stringResource(R.string.settings_inc_weight)) }
                            OutlinedButton(
                                onClick = { vm.save(p.copy(weightKg = (p.weightKg - 0.5))) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
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

//            PreferenceCard(title = stringResource(R.string.settings_theme_title)) {
//                OptionRow {
//                    OptionButton(stringResource(R.string.settings_theme_light)) {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                        prefs.setTheme("light")
//                        (context as? Activity)?.recreate()
//                    }
//                    OptionButton(stringResource(R.string.settings_theme_dark)) {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                        prefs.setTheme("dark")
//                        (context as? Activity)?.recreate()
//                    }
//                    OptionButton(stringResource(R.string.settings_theme_system)) {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                        prefs.setTheme("system")
//                        (context as? Activity)?.recreate()
//                    }
//                }
//            }

            if (s.saving) {
                Text(stringResource(R.string.settings_saving), color = Color.White.copy(alpha = 0.8f))
            }
            Button(
                onClick = { vm.reset { restartApp() } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AiPalette.DeepAccent,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large,
                enabled = !s.saving
            ) {
                Text("\u0417\u0430\u043d\u043e\u0432\u043e \u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u044f", fontWeight = FontWeight.Bold)
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
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AiPalette.OnGradient
            )
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
        )
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun settingsCardColors() = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))

@Composable
private fun settingsCardElevation() = CardDefaults.cardElevation(defaultElevation = 6.dp)

@Composable
private fun ProfileStatsGrid(profile: com.example.aihighpulse.shared.domain.model.Profile) {
    val activeDays = profile.weeklySchedule.count { it.value }
    val bmi = profile.weightKg / ((profile.heightCm / 100.0) * (profile.heightCm / 100.0))
    val bmiLabel = String.format("%.1f", bmi)
    val goalLabel = when (profile.goal) {
        com.example.aihighpulse.shared.domain.model.Goal.LOSE_FAT -> "Fat loss"
        com.example.aihighpulse.shared.domain.model.Goal.GAIN_MUSCLE -> "Muscle gain"
        com.example.aihighpulse.shared.domain.model.Goal.MAINTAIN -> "Maintain"
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatPill(
                icon = Icons.Filled.Scale,
                label = stringResource(R.string.settings_weight_label),
                value = stringResource(R.string.settings_weight_value, profile.weightKg),
                modifier = Modifier.weight(1f)
            )
            ProfileStatPill(
                icon = Icons.Filled.Straighten,
                label = stringResource(R.string.settings_height_label),
                value = stringResource(R.string.settings_height_value, profile.heightCm),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatPill(
                icon = Icons.Filled.FitnessCenter,
                label = stringResource(R.string.settings_experience_label),
                value = stringResource(R.string.settings_experience_value, profile.experienceLevel),
                modifier = Modifier.weight(1f)
            )
            ProfileStatPill(
                icon = Icons.Filled.Schedule,
                label = stringResource(R.string.settings_days_label),
                value = activeDays.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatPill(
                icon = Icons.Filled.MonitorHeart,
                label = stringResource(R.string.settings_bmi_label),
                value = bmiLabel,
                modifier = Modifier.weight(1f)
            )
            ProfileStatPill(
                icon = Icons.Filled.Wallet,
                label = stringResource(R.string.settings_goal_label),
                value = goalLabel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProfileStatPill(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.18f)),
        border = BorderStroke(1.dp, Color(0xFFD9DBE0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = AiPalette.Primary.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AiPalette.DeepAccent,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF4B4B61))
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C28),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


