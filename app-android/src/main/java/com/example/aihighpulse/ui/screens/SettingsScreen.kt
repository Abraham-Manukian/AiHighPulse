package com.example.aihighpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.aihighpulse.ui.components.PlaceholderScreen
import com.example.aihighpulse.ui.vm.SettingsViewModel

@Composable
fun SettingsScreen() {
    val vm: SettingsViewModel = koinViewModel()
    val s by vm.state.collectAsState()
    val p = s.profile
    if (p == null) {
        PlaceholderScreen("Профиль не заполнен", listOf("Перейдите в онбординг"))
        return
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Профиль", style = MaterialTheme.typography.titleLarge)
        Text("Возраст: ${'$'}{p.age}")
        Text("Рост: ${'$'}{p.heightCm} см  Вес: ${'$'}{p.weightKg} кг")
        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { vm.save(p.copy(weightKg = (p.weightKg + 0.5))) }) { Text("+0.5 кг") }
            OutlinedButton(onClick = { vm.save(p.copy(weightKg = (p.weightKg - 0.5))) }) { Text("-0.5 кг") }
        }
        if (s.saving) Text("Сохранение...", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
