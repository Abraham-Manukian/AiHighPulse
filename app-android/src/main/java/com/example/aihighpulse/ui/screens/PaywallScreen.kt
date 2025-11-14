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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.ui.vm.PaywallViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon

@Composable
fun PaywallScreen() {
    val vm: PaywallViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }
    val selected = remember { mutableStateOf("monthly") }
    val ctaScale by animateFloatAsState(targetValue = if (selected.value == "yearly") 1.02f else 1.0f, animationSpec = tween(250), label = "ctaScale")
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(300, easing = FastOutSlowInEasing))
        ) {
            Text(stringResource(R.string.paywall_title), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        }
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 80))
        ) {
            Text(stringResource(R.string.paywall_desc), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (s.active) {
            Card { Text(stringResource(R.string.paywall_active), Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary) }
        } else {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    val monthlySelected = selected.value == "monthly"
                    val yearlySelected = selected.value == "yearly"
                    val selectedContainer = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    val shape = RoundedCornerShape(12.dp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                    ) {
                        Card(
                            shape = shape,
                            colors = CardDefaults.cardColors(containerColor = if (monthlySelected) selectedContainer else Color.Transparent),
                            onClick = { selected.value = "monthly" }
                        ) {
                            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                RadioButton(selected = monthlySelected, onClick = { selected.value = "monthly" })
                                Icon(imageVector = Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column { Text(stringResource(R.string.paywall_monthly)); Text("$9.99") }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                    ) {
                        Card(
                            shape = shape,
                            colors = CardDefaults.cardColors(containerColor = if (yearlySelected) selectedContainer else Color.Transparent),
                            onClick = { selected.value = "yearly" }
                        ) {
                            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                RadioButton(selected = yearlySelected, onClick = { selected.value = "yearly" })
                                Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text(stringResource(R.string.paywall_yearly))
                                    Text("$99.99")
                                    AnimatedVisibility(visible = true, enter = fadeIn(animationSpec = tween(200, delayMillis = 80))) {
                                        Text("Лучшее предложение", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
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
                    modifier = Modifier.fillMaxWidth().scale(ctaScale)
                ) { Text(stringResource(R.string.paywall_cta)) }
            }
        }
    }
}
