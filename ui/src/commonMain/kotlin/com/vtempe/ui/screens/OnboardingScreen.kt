@file:OptIn(
    org.jetbrains.compose.resources.ExperimentalResourceApi::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.vtempe.ui.screens
import com.vtempe.ui.*

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
	import androidx.compose.ui.text.font.FontWeight
	import androidx.compose.ui.unit.dp
	import com.vtempe.core.designsystem.components.BrandScreen
	import com.vtempe.core.designsystem.theme.AiPalette
	import com.vtempe.shared.domain.model.Goal
	import com.vtempe.shared.domain.model.Sex
	import com.vtempe.ui.util.kmpFormat
	import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(
    onDone: () -> Unit = {},
    presenter: OnboardingPresenter = rememberOnboardingPresenter()
) {
    val state by presenter.state.collectAsState()

    val equipmentOptions = listOf(
        stringResource(Res.string.equipment_dumbbells),
        stringResource(Res.string.equipment_barbell),
        stringResource(Res.string.equipment_kettlebell),
        stringResource(Res.string.equipment_bands),
        stringResource(Res.string.equipment_bench),
        stringResource(Res.string.equipment_pullup_bar),
        stringResource(Res.string.equipment_trx),
        stringResource(Res.string.equipment_mat),
        stringResource(Res.string.equipment_cardio)
    )
    val dayLabels = listOf(
        "Mon" to stringResource(Res.string.day_mon_short),
        "Tue" to stringResource(Res.string.day_tue_short),
        "Wed" to stringResource(Res.string.day_wed_short),
        "Thu" to stringResource(Res.string.day_thu_short),
        "Fri" to stringResource(Res.string.day_fri_short),
        "Sat" to stringResource(Res.string.day_sat_short),
        "Sun" to stringResource(Res.string.day_sun_short)
    )
    val languageOptions = listOf(
        "system" to stringResource(Res.string.settings_language_system),
        "en-US" to stringResource(Res.string.language_en),
        "ru-RU" to stringResource(Res.string.language_ru)
    )

    val glassBg = Color.White.copy(alpha = 0.18f)
    val glassBorder = Color.White.copy(alpha = 0.35f)
    val inputColors = TextFieldDefaults.colors(
        focusedIndicatorColor = glassBorder,
        unfocusedIndicatorColor = glassBorder.copy(alpha = 0.6f),
        cursorColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.85f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledTextColor = Color.White.copy(alpha = 0.6f),
        focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
    )

    val progress = (state.currentStep + 1).toFloat() / ONBOARDING_TOTAL_STEPS.toFloat()
    val isLastStep = state.currentStep >= ONBOARDING_TOTAL_STEPS - 1

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
                stringResource(Res.string.onboard_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(stringResource(Res.string.onboard_subtitle), color = Color.White.copy(alpha = 0.85f))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
	                Text(
	                    text = stringResource(Res.string.onboard_step_counter).kmpFormat(
	                        state.currentStep + 1,
	                        ONBOARDING_TOTAL_STEPS
	                    ),
	                    color = Color.White.copy(alpha = 0.8f),
	                    style = MaterialTheme.typography.bodyMedium,
	                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    trackColor = Color.White.copy(alpha = 0.15f),
                    color = AiPalette.DeepAccent
                )
            }

            Crossfade(targetState = state.currentStep, label = "onboarding_steps") { step ->
                when (step) {
                    0 -> StepCard {
                        Text(stringResource(Res.string.label_language), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            languageOptions.forEach { (tag, label) ->
                                FilterChip(
                                    selected = state.languageTag == tag,
                                    onClick = { presenter.setLanguage(tag) },
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
                                value = state.age,
                                onValueChange = { v -> presenter.update { st -> st.copy(age = v.filter { it.isDigit() }.take(2)) } },
                                label = { Text(stringResource(Res.string.label_age)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(glassBg),
                                colors = inputColors
                            )
                            OutlinedTextField(
                                value = state.heightCm,
                                onValueChange = { v -> presenter.update { st -> st.copy(heightCm = v.filter { it.isDigit() }.take(3)) } },
                                label = { Text(stringResource(Res.string.label_height_cm)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(glassBg),
                                colors = inputColors
                            )
                            OutlinedTextField(
                                value = state.weightKg,
                                onValueChange = { v -> presenter.update { st -> st.copy(weightKg = v.filter { it.isDigit() || it == '.' }.take(5)) } },
                                label = { Text(stringResource(Res.string.label_weight_kg)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(glassBg),
                                colors = inputColors
                            )
                        }

                        Text(stringResource(Res.string.label_sex), color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(Sex.MALE, Sex.FEMALE, Sex.OTHER).forEach { option ->
                                val label = when (option) {
                                    Sex.MALE -> stringResource(Res.string.sex_male)
                                    Sex.FEMALE -> stringResource(Res.string.sex_female)
                                    Sex.OTHER -> stringResource(Res.string.sex_other)
                                }
                                FilterChip(
                                    selected = state.sex == option,
                                    onClick = { presenter.update { it.copy(sex = option) } },
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
                        Text(stringResource(Res.string.label_goal), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Goal.values().forEach { goal ->
                                val label = when (goal) {
                                    Goal.LOSE_FAT -> stringResource(Res.string.goal_lose_fat)
                                    Goal.MAINTAIN -> stringResource(Res.string.goal_maintain)
                                    Goal.GAIN_MUSCLE -> stringResource(Res.string.goal_gain_muscle)
                                }
                                FilterChip(
                                    selected = state.goal == goal,
                                    onClick = { presenter.update { it.copy(goal = goal) } },
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
	                        Text(
	                            stringResource(Res.string.label_experience).kmpFormat(state.experienceLevel),
	                            color = Color.White
	                        )
                        Slider(
                            value = state.experienceLevel.toFloat(),
                            onValueChange = { lvl -> presenter.update { it.copy(experienceLevel = lvl.toInt().coerceIn(1, 5)) } },
                            valueRange = 1f..5f,
                            steps = 3
                        )
                    }

                    3 -> StepCard {
                        Text(stringResource(Res.string.label_equipment_presets), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            equipmentOptions.forEach { option ->
                                ChipToggle(
                                    label = option,
                                    selected = state.selectedEquipment.contains(option),
                                    onClick = { presenter.toggleEquipment(option) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = state.customEquipment,
                            onValueChange = { presenter.setCustomEquipment(it) },
                            label = { Text(stringResource(Res.string.label_equipment_manual)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassBg),
                            colors = inputColors
                        )
                    }

                    4 -> StepCard {
                        Text(stringResource(Res.string.label_dietary_prefs), color = Color.White)
                        OutlinedTextField(
                            value = state.dietaryPreferences,
                            onValueChange = { presenter.update { st -> st.copy(dietaryPreferences = it) } },
                            label = { Text(stringResource(Res.string.label_dietary_prefs)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassBg),
                            colors = inputColors
                        )
                        OutlinedTextField(
                            value = state.allergies,
                            onValueChange = { presenter.update { st -> st.copy(allergies = it) } },
                            label = { Text(stringResource(Res.string.label_allergies)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassBg),
                            colors = inputColors
                        )
                    }

                    else -> StepCard {
                        Text(stringResource(Res.string.label_weekdays), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            dayLabels.forEach { (day, label) ->
                                val selected = state.days[day] == true
                                ChipToggle(
                                    label = label,
                                    selected = selected,
                                    onClick = { presenter.update { st -> st.copy(days = st.days + (day to !selected)) } }
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
                if (state.currentStep > 0) {
                    OutlinedButton(
                        onClick = { presenter.prevStep() },
                        enabled = !state.saving,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, glassBorder)
                    ) { Text(stringResource(Res.string.action_back)) }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                Button(
                    onClick = {
                        if (isLastStep) presenter.save(onDone) else presenter.nextStep()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.saving,
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AiPalette.DeepAccent,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isLastStep) stringResource(Res.string.onboard_cta) else stringResource(Res.string.action_next))
                }
            }

            if (state.saving) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
                    Text(text = stringResource(Res.string.settings_saving), color = Color.White.copy(alpha = 0.85f))
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
private fun onboardingCardColors() = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))

@Composable
private fun onboardingCardElevation() = CardDefaults.cardElevation(defaultElevation = 8.dp)

