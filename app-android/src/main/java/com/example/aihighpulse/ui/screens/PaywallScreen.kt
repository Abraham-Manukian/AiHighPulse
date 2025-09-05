package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.ui.vm.PaywallViewModel

@Composable
fun PaywallScreen() {
    val vm: PaywallViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Подписка Pro", style = MaterialTheme.typography.headlineSmall)
        Text("Доступ к персональным планам, аналитике сна и т.д.")
        if (s.active) {
            Card { Text("Подписка активна", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary) }
        } else {
            Button(onClick = { /* integrate Billing later */ }) { Text("Активировать (заглушка)") }
        }
    }
}
