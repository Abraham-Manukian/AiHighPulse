package com.example.aihighpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.theme.AiGradients
import com.example.aihighpulse.shared.domain.model.Goal
import com.example.aihighpulse.shared.domain.model.Sex
import com.example.aihighpulse.ui.vm.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val vm: OnboardingViewModel = koinViewModel()
    val s by vm.state.collectAsState()

    val mounted = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mounted.value = true }

    val equipmentOptions = listOf(
        "Гантели",
        "Штанга",
        "Турник",
        "Скакалка",
        "Эспандеры",
        "Коврик",
        "Петли TRX",
        "Беговая дорожка",
        "Велотренажёр"
    )

    val dayLabels = listOf(
        "Mon" to stringResource(R.string.day_mon_short),
        "Tue" to stringResource(R.string.day_tue_short),
        "Wed" to stringResource(R.string.day_wed_short),
        "Thu" to stringResource(R.string.day_thu_short),
        "Fri" to stringResource(R.string.day_fri_short),
        "Sat" to stringResource(R.string.day_sat_short),
        "Sun" to stringResource(R.string.day_sun_short)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AiGradients.purpleWave())
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.onboard_title), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Text(stringResource(R.string.onboard_subtitle), color = Color.White.copy(alpha = 0.85f))

        val languageOptions = listOf(
            "system" to stringResource(R.string.settings_language_system),
            "en-US" to stringResource(R.string.language_en),
            "ru-RU" to stringResource(R.string.language_ru)
        )
        AnimatedVisibility(
            visible = mounted.value,
            enter = fadeIn(animationSpec = tween(350)) + slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(350, easing = FastOutSlowInEasing))
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.label_language), style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        languageOptions.forEach { (tag, label) ->
                            FilterChip(
                                selected = s.languageTag == tag,
                                onClick = { vm.setLanguage(tag) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = mounted.value,
            enter = fadeIn(animationSpec = tween(450, delayMillis = 120)) + slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(450, easing = FastOutSlowInEasing))
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = s.age,
                            onValueChange = { v -> vm.update { st -> st.copy(age = v.filter { it.isDigit() }.take(2)) } },
                            label = { Text(stringResource(R.string.label_age)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = s.heightCm,
                            onValueChange = { v -> vm.update { st -> st.copy(heightCm = v.filter { it.isDigit() }.take(3)) } },
                            label = { Text(stringResource(R.string.label_height_cm)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = s.weightKg,
                            onValueChange = { v -> vm.update { st -> st.copy(weightKg = v.filter { it.isDigit() || it == '.' }.take(5)) } },
                            label = { Text(stringResource(R.string.label_weight_kg)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(stringResource(R.string.label_sex))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val items = listOf(
                            Sex.MALE to stringResource(R.string.sex_male),
                            Sex.FEMALE to stringResource(R.string.sex_female),
                            Sex.OTHER to stringResource(R.string.sex_other)
                        )
                        items.forEach { (value, label) ->
                            FilterChip(selected = s.sex == value, onClick = { vm.update { it.copy(sex = value) } }, label = { Text(label) })
                        }
                    }

                    Text(stringResource(R.string.label_goal))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val items = listOf(
                            Goal.LOSE_FAT to stringResource(R.string.goal_lose_fat),
                            Goal.MAINTAIN to stringResource(R.string.goal_maintain),
                            Goal.GAIN_MUSCLE to stringResource(R.string.goal_gain_muscle)
                        )
                        items.forEach { (value, label) ->
                            FilterChip(selected = s.goal == value, onClick = { vm.update { it.copy(goal = value) } }, label = { Text(label) })
                        }
                    }

                    Text(stringResource(R.string.label_experience, s.experienceLevel))
                    Slider(
                        value = s.experienceLevel.toFloat(),
                        onValueChange = { v -> vm.update { st -> st.copy(experienceLevel = v.toInt().coerceIn(1, 5)) } },
                        valueRange = 1f..5f,
                        steps = 3
                    )

                    Text(stringResource(R.string.label_equipment_presets))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        equipmentOptions.forEach { name ->
                            val selected = name in s.selectedEquipment
                            FilterChip(
                                selected = selected,
                                onClick = { vm.toggleEquipment(name) },
                                label = { Text(name) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = s.customEquipment,
                        onValueChange = vm::setCustomEquipment,
                        label = { Text(stringResource(R.string.label_equipment_manual)) },
                        placeholder = { Text(stringResource(R.string.placeholder_equipment_manual)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = s.dietaryPreferences,
                            onValueChange = { v -> vm.update { st -> st.copy(dietaryPreferences = v.take(120)) } },
                            label = { Text(stringResource(R.string.label_dietary_prefs)) },
                            placeholder = { Text(stringResource(R.string.placeholder_dietary_prefs)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = s.allergies,
                            onValueChange = { v -> vm.update { st -> st.copy(allergies = v.take(120)) } },
                            label = { Text(stringResource(R.string.label_allergies)) },
                            placeholder = { Text(stringResource(R.string.placeholder_allergies)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(stringResource(R.string.label_weekdays))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dayLabels.forEach { (key, label) ->
                            val checked = s.days[key] == true
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                                    .background(if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { vm.update { it.copy(days = it.days + (key to !checked)) } }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) { Text(label) }
                        }
                    }
                }
            }
        }

        if (s.error != null) Text(stringResource(R.string.error_invalid_input), color = MaterialTheme.colorScheme.error)
        Button(enabled = !s.saving, onClick = { vm.save(onSuccess = onDone) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.save_and_continue))
        }
    }
}

@Composable
private fun FeatureRow(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.size(12.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium)
    }
}
