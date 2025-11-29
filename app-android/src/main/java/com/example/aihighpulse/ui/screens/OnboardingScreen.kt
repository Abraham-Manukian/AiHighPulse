package com.example.aihighpulse.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.theme.AiPalette
import com.example.aihighpulse.shared.domain.model.Goal
import com.example.aihighpulse.shared.domain.model.Sex
import com.example.aihighpulse.ui.vm.ONBOARDING_TOTAL_STEPS
import com.example.aihighpulse.ui.vm.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val vm: OnboardingViewModel = koinViewModel()
    val s by vm.state.collectAsState()

    val equipmentOptions = listOf(
        stringResource(R.string.equipment_dumbbells),
        stringResource(R.string.equipment_barbell),
        stringResource(R.string.equipment_kettlebell),
        stringResource(R.string.equipment_bands),
        stringResource(R.string.equipment_bench),
        stringResource(R.string.equipment_pullup_bar),
        stringResource(R.string.equipment_trx),
        stringResource(R.string.equipment_mat),
        stringResource(R.string.equipment_cardio)
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

    val languageOptions = listOf(
        "system" to stringResource(R.string.settings_language_system),
        "en-US" to stringResource(R.string.language_en),
        "ru-RU" to stringResource(R.string.language_ru)
    )
    val glassBg = Color.White.copy(alpha = 0.18f)
    val glassBorder = Color.White.copy(alpha = 0.35f)
    val inputColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = glassBorder,
        unfocusedBorderColor = glassBorder.copy(alpha = 0.6f),
        cursorColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.85f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledTextColor = Color.White.copy(alpha = 0.6f),
        focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
    )

    val progress = (s.currentStep + 1).toFloat() / ONBOARDING_TOTAL_STEPS.toFloat()
    val isLastStep = s.currentStep >= ONBOARDING_TOTAL_STEPS - 1

    BrandScreen(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.onboard_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(stringResource(R.string.onboard_subtitle), color = Color.White.copy(alpha = 0.85f))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.onboard_step_counter, s.currentStep + 1, ONBOARDING_TOTAL_STEPS),
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    trackColor = Color.White.copy(alpha = 0.15f),
                    color = AiPalette.DeepAccent
                )
            }

            Crossfade(targetState = s.currentStep, label = "onboarding_steps") { step ->
                when (step) {
                    0 -> StepCard {
                        Text(stringResource(R.string.label_language), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            languageOptions.forEach { (tag, label) ->
                                FilterChip(
                                    selected = s.languageTag == tag,
                                    onClick = { vm.setLanguage(tag) },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = glassBg,
                                        selectedLabelColor = Color.White,
                                        containerColor = glassBg.copy(alpha = 0.12f),
                                        labelColor = Color.White
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                    1 -> StepCard {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = s.age,
                                onValueChange = { v -> vm.update { st -> st.copy(age = v.filter { it.isDigit() }.take(2)) } },
                                label = { Text(stringResource(R.string.label_age)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(glassBg),
                                colors = inputColors
                            )
                            OutlinedTextField(
                                value = s.heightCm,
                                onValueChange = { v -> vm.update { st -> st.copy(heightCm = v.filter { it.isDigit() }.take(3)) } },
                                label = { Text(stringResource(R.string.label_height_cm)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(glassBg),
                                colors = inputColors
                            )
                            OutlinedTextField(
                                value = s.weightKg,
                                onValueChange = { v -> vm.update { st -> st.copy(weightKg = v.filter { it.isDigit() || it == '.' }.take(5)) } },
                                label = { Text(stringResource(R.string.label_weight_kg)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(glassBg),
                                colors = inputColors
                            )
                        }

                        Text(stringResource(R.string.label_sex), color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(Sex.MALE, Sex.FEMALE, Sex.OTHER).forEach { option ->
                                val label = when (option) {
                                    Sex.MALE -> stringResource(R.string.sex_male)
                                    Sex.FEMALE -> stringResource(R.string.sex_female)
                                    Sex.OTHER -> stringResource(R.string.sex_other)
                                }
                                FilterChip(
                                    selected = s.sex == option,
                                    onClick = { vm.update { it.copy(sex = option) } },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = glassBg,
                                        selectedLabelColor = Color.White,
                                        containerColor = glassBg.copy(alpha = 0.12f),
                                        labelColor = Color.White
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                    2 -> StepCard {
                        Text(stringResource(R.string.label_goal), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Goal.values().forEach { goal ->
                                val label = when (goal) {
                                    Goal.LOSE_FAT -> stringResource(R.string.goal_lose_fat)
                                    Goal.MAINTAIN -> stringResource(R.string.goal_maintain)
                                    Goal.GAIN_MUSCLE -> stringResource(R.string.goal_gain_muscle)
                                }
                                FilterChip(
                                    selected = s.goal == goal,
                                    onClick = { vm.update { it.copy(goal = goal) } },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = glassBg,
                                        selectedLabelColor = Color.White,
                                        containerColor = glassBg.copy(alpha = 0.12f),
                                        labelColor = Color.White
                                    ),
                                    border = null
                                )
                            }
                        }
                        Text(stringResource(R.string.label_experience, s.experienceLevel), color = Color.White)
                        Slider(
                            value = s.experienceLevel.toFloat(),
                            onValueChange = { lvl -> vm.update { it.copy(experienceLevel = lvl.toInt().coerceIn(1, 5)) } },
                            valueRange = 1f..5f,
                            steps = 3
                        )
                    }
                    3 -> StepCard {
                        Text(stringResource(R.string.label_equipment_presets), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            equipmentOptions.forEach { option ->
                                ChipToggle(
                                    label = option,
                                    selected = s.selectedEquipment.contains(option),
                                    onClick = { vm.toggleEquipment(option) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = s.customEquipment,
                            onValueChange = { vm.setCustomEquipment(it) },
                            label = { Text(stringResource(R.string.label_equipment_manual)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassBg),
                            colors = inputColors
                        )
                    }
                    4 -> StepCard {
                        Text(stringResource(R.string.label_dietary_prefs), color = Color.White)
                        OutlinedTextField(
                            value = s.dietaryPreferences,
                            onValueChange = { vm.update { st -> st.copy(dietaryPreferences = it) } },
                            label = { Text(stringResource(R.string.label_dietary_prefs)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassBg),
                            colors = inputColors
                        )
                        OutlinedTextField(
                            value = s.allergies,
                            onValueChange = { vm.update { st -> st.copy(allergies = it) } },
                            label = { Text(stringResource(R.string.label_allergies)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassBg),
                            colors = inputColors
                        )
                    }
                    else -> StepCard {
                        Text(stringResource(R.string.label_weekdays), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            dayLabels.forEach { (day, label) ->
                                val selected = s.days[day] == true
                                ChipToggle(
                                    label = label,
                                    selected = selected,
                                    onClick = { vm.update { st -> st.copy(days = st.days + (day to !selected)) } }
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (s.currentStep > 0) {
                    OutlinedButton(
                        onClick = { vm.prevStep() },
                        enabled = !s.saving,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, glassBorder)
                    ) { Text(stringResource(R.string.action_back)) }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                Button(
                    onClick = {
                        if (isLastStep) vm.save(onDone) else vm.nextStep()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !s.saving,
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AiPalette.DeepAccent,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isLastStep) stringResource(R.string.onboard_cta) else stringResource(R.string.action_next))
                }
            }

            if (s.saving) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
                    Text(text = stringResource(R.string.settings_saving), color = Color.White.copy(alpha = 0.85f))
                }
            }
        }
    }
}

@Composable
private fun StepCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = onboardingCardColors(),
        elevation = onboardingCardElevation(),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            content()
        }
    }
}

@Composable
private fun ChipToggle(label: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)
    val borderColor = Color.White.copy(alpha = if (selected) 0.55f else 0.2f)
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun onboardingCardColors() = CardDefaults.cardColors(
    containerColor = Color.White.copy(alpha = 0.14f),
    contentColor = Color.White
)

@Composable
private fun onboardingCardElevation() = CardDefaults.cardElevation(defaultElevation = 0.dp)
