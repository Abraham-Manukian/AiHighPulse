package com.example.aihighpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.core.designsystem.components.BrandScreen
import com.example.aihighpulse.core.designsystem.theme.AiPalette
import com.example.aihighpulse.ui.vm.PaywallViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaywallScreen() {
    val vm: PaywallViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }
    val selected = remember { mutableStateOf("monthly") }
    val ctaScale by animateFloatAsState(
        targetValue = if (selected.value == "yearly") 1.02f else 1.0f,
        animationSpec = tween(250),
        label = "ctaScale"
    )
    val contentColor = Color.White

    BrandScreen(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            ) {
                Text(
                    stringResource(R.string.paywall_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 80))
            ) {
                Text(
                    stringResource(R.string.paywall_desc),
                    color = contentColor.copy(alpha = 0.85f)
                )
            }
            if (s.active) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        stringResource(R.string.paywall_active),
                        Modifier.padding(16.dp),
                        color = contentColor
                    )
                }
            } else {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PaywallPlanCard(
                                plan = PaywallPlan(
                                    id = "monthly",
                                    title = stringResource(R.string.paywall_monthly),
                                    price = "$9.99",
                                    icon = Icons.Filled.DateRange
                                ),
                                selectedId = selected.value,
                                onSelect = { selected.value = it },
                                modifier = Modifier.weight(1f)
                            )
                            PaywallPlanCard(
                                plan = PaywallPlan(
                                    id = "yearly",
                                    title = stringResource(R.string.paywall_yearly),
                                    price = "$99.99",
                                    icon = Icons.Filled.Star,
                                    highlight = stringResource(R.string.paywall_best_value)
                                ),
                                selectedId = selected.value,
                                onSelect = { selected.value = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(animationSpec = tween(250, delayMillis = 60)) + fadeIn(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Button(
                        onClick = { /* integrate Billing later */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AiPalette.DeepAccent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(ctaScale)
                    ) { Text(stringResource(R.string.paywall_cta)) }
                }
            }
        }
    }
}

@Immutable
private data class PaywallPlan(
    val id: String,
    val title: String,
    val price: String,
    val icon: ImageVector,
    val highlight: String? = null
)

@Composable
private fun PaywallPlanCard(
    plan: PaywallPlan,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = selectedId == plan.id
    val selectedBg = if (selected) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.06f)
    val contentColor = Color(0xFF1C1C28)
    val shape = RoundedCornerShape(18.dp)
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = selectedBg),
        border = BorderStroke(1.dp, Color(0xFFE3E3EA)),
        onClick = { onSelect(plan.id) },
        modifier = modifier
    ) {
        Column(
            Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selected,
                    onClick = { onSelect(plan.id) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AiPalette.DeepAccent,
                        unselectedColor = Color(0xFF9AA0B5)
                    )
                )
                Icon(imageVector = plan.icon, contentDescription = null, tint = AiPalette.DeepAccent)
                Text(
                    plan.title,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
            Text(
                plan.price,
                color = Color(0xFF4B4B61),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            if (plan.highlight != null) {
                Text(
                    plan.highlight,
                    color = AiPalette.DeepAccent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}
