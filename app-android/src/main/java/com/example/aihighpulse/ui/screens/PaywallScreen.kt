package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aihighpulse.R
import com.example.aihighpulse.ui.vm.PaywallViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaywallScreen() {
    val vm: PaywallViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }
    val selected = remember { mutableStateOf("monthly") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.paywall_title), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.paywall_desc), color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (s.active) {
            Card { Text(stringResource(R.string.paywall_active), Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary) }
        } else {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RadioButton(selected = selected.value == "monthly", onClick = { selected.value = "monthly" })
                        Column { Text(stringResource(R.string.paywall_monthly)); Text("$3.99") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RadioButton(selected = selected.value == "yearly", onClick = { selected.value = "yearly" })
                        Column { Text(stringResource(R.string.paywall_yearly)); Text("$29.99") }
                    }
                }
            }
            Button(onClick = { /* integrate Billing later */ }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.paywall_cta)) }
        }
    }
}
