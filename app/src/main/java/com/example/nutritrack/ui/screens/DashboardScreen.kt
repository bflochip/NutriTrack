package com.example.nutritrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nutritrack.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSearch: () -> Unit
) {
    val calories by viewModel.totalCalories.collectAsState()
    val protein by viewModel.totalProtein.collectAsState()
    val water by viewModel.waterIntakeOz.collectAsState()
    val latestWeight by viewModel.latestWeight.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToSearch) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Daily Summary", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Macro Cards
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                MacroCard("Calories", "${calories.toInt()} kcal")
                MacroCard("Protein", "${protein.toInt()} g")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Water Intake
            Text(text = "Water Intake: ${water.toInt()} oz", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { viewModel.addWater(8.0) }, modifier = Modifier.padding(top = 8.dp)) {
                Text("+8 oz Water")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Weight log
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Latest Weight", style = MaterialTheme.typography.titleMedium)
                    if (latestWeight != null) {
                        Text("${latestWeight?.weightLbs} lbs")
                    } else {
                        Text("No weight logged yet.")
                    }
                    Button(onClick = { viewModel.logWeight(150.0) }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Log Weight (Demo 150 lbs)")
                    }
                }
            }
        }
    }
}

@Composable
fun MacroCard(title: String, value: String) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
