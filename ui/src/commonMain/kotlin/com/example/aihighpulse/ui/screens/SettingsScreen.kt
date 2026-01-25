@file:OptIn(org.jetbrains.compose.resources.ExperimentalResourceApi::class)

package com.example.aihighpulse.ui.screens
import com.example.aihighpulse.ui.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.theme.AiPalette
import com.example.aihighpulse.shared.domain.model.Profile
import com.example.aihighpulse.ui.platform.SettingsPlatformActions
import com.example.aihighpulse.ui.platform.rememberSettingsPlatformActions
import com.example.aihighpulse.ui.util.kmpFormat
import com.vtempe.ui.LocalBottomBarHeight
import com.vtempe.ui.LocalTopBarHeight
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    onEditProfile: () -> Unit = {},
    presenter: SettingsPresenter = rememberSettingsPresenter(),
    platformActions: SettingsPlatformActions = rememberSettingsPlatformActions()
) {
    val state by presenter.state.collectAsState()
    val profile = state.profile
    
    val topBarHeight = LocalTopBarHeight.current
    val bottomBarHeight = LocalBottomBarHeight.current

    if (profile == null) {
        LaunchedEffect(Unit) { presenter.refresh() }
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(topBarHeight + 16.dp))
            
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
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Avatar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    stringResource(Res.string.settings_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                                Text(
                                    stringResource(Res.string.settings_age).kmpFormat(profile.age),
                                    color = contentColor.copy(alpha = 0.8f)
                                )
                                Text(
                                    stringResource(Res.string.settings_height_weight).kmpFormat(
                                        profile.heightCm,
                                        profile.weightKg
                                    ),
                                    color = contentColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ProfileStatsGrid(profile = profile)
                        OutlinedButton(
                            onClick = onEditProfile,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) { Text(stringResource(Res.string.settings_edit_profile)) }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { presenter.save(profile.copy(weightKg = profile.weightKg + 0.5)) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
                            ) { Text(stringResource(Res.string.settings_inc_weight)) }
                            OutlinedButton(
                                onClick = { presenter.save(profile.copy(weightKg = profile.weightKg - 0.5)) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
                            ) { Text(stringResource(Res.string.settings_dec_weight)) }
                        }
                    }
                }
            }

            PreferenceCard(title = stringResource(Res.string.settings_units_title)) {
                OptionRow {
                    OptionButton(stringResource(Res.string.settings_units_metric)) { presenter.setUnits("metric") }
                    OptionButton(stringResource(Res.string.settings_units_imperial)) { presenter.setUnits("imperial") }
                }
            }

            PreferenceCard(title = stringResource(Res.string.settings_language_title)) {
                OptionRow {
                    OptionButton(stringResource(Res.string.settings_language_ru)) {
                        presenter.setLanguage("ru")
                        platformActions.restartApp()
                    }
                    OptionButton(stringResource(Res.string.settings_language_system)) {
                        presenter.setLanguage(null)
                        platformActions.restartApp()
                    }
                }
            }

            if (state.saving) {
                Text(stringResource(Res.string.settings_saving), color = Color.White.copy(alpha = 0.8f))
            }
            Button(
                onClick = { presenter.reset { platformActions.restartApp() } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AiPalette.DeepAccent,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large,
                enabled = !state.saving
            ) {
                Text("\u0417\u0430\u043d\u043e\u0432\u043e \u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u044f", fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(bottomBarHeight + 16.dp))
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
private fun ProfileStatsGrid(profile: Profile) {
    val activeDays = profile.weeklySchedule.count { it.value }
    val bmi = profile.weightKg / ((profile.heightCm / 100.0) * (profile.heightCm / 100.0))
    val bmiLabel = ((bmi * 10).roundToInt() / 10.0).toString()
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
                label = stringResource(Res.string.settings_weight_label),
                value = stringResource(Res.string.settings_weight_value).kmpFormat(profile.weightKg),
                modifier = Modifier.weight(1f)
            )
            ProfileStatPill(
                icon = Icons.Filled.Straighten,
                label = stringResource(Res.string.settings_height_label),
                value = stringResource(Res.string.settings_height_value).kmpFormat(profile.heightCm),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatPill(
                icon = Icons.Filled.FitnessCenter,
                label = stringResource(Res.string.settings_experience_label),
                value = stringResource(Res.string.settings_experience_value).kmpFormat(profile.experienceLevel),
                modifier = Modifier.weight(1f)
            )
            ProfileStatPill(
                icon = Icons.Filled.Schedule,
                label = stringResource(Res.string.settings_days_label),
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
                label = stringResource(Res.string.settings_bmi_label),
                value = bmiLabel,
                modifier = Modifier.weight(1f)
            )
            ProfileStatPill(
                icon = Icons.Filled.Wallet,
                label = stringResource(Res.string.settings_goal_label),
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
                    color = Color(0xFF1C1C28)
                )
            }
        }
    }
}
