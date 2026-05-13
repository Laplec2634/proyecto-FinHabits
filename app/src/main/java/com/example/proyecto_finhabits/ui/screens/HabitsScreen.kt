package com.example.proyecto_finhabits.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyecto_finhabits.data.entities.Habit
import com.example.proyecto_finhabits.ui.components.EmptyState
import com.example.proyecto_finhabits.ui.components.FinHabitsTopBar
import com.example.proyecto_finhabits.ui.navigation.Routes
import com.example.proyecto_finhabits.viewmodel.HabitViewModel
import com.example.proyecto_finhabits.viewmodel.HabitWithProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(nav: NavHostController, vm: HabitViewModel) {
    val habits by vm.habitsWithProgress.collectAsState()
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    Scaffold(
        topBar = { FinHabitsTopBar("Mis Hábitos 🔥") },
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate(Routes.ADD_HABIT) }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo hábito")
            }
        },
        bottomBar = { BottomNav(nav, Routes.HABITS) }
    ) { padding ->
        if (habits.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState("Aún no tienes hábitos.\n¡Crea tu primer hábito financiero!", "🌱")
            }
        } else {
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(habits, key = { it.habit.id }) { hwp ->
                    HabitCard(
                        hwp = hwp,
                        onToggle = { vm.toggleCompletion(hwp.habit) },
                        onEdit = { nav.navigate(Routes.editHabit(hwp.habit.id)) },
                        onDelete = { habitToDelete = hwp.habit }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    habitToDelete?.let { h ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Eliminar hábito") },
            text = { Text("¿Eliminar \"${h.name}\"? Se perderá toda su racha y progreso.") },
            confirmButton = {
                TextButton(onClick = { vm.delete(h); habitToDelete = null }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { habitToDelete = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun HabitCard(hwp: HabitWithProgress, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val h = hwp.habit
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hwp.isCompletedToday) MaterialTheme.colorScheme.primaryContainer
                             else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (hwp.isCompletedToday) "✅" else "🎯", fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(h.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (h.description.isNotBlank()) {
                        Text(h.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${h.frequency.label} • Racha: ${h.streak} 🔥", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp)) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error) }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { hwp.weeklyRate },
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text("${(hwp.weeklyRate * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hwp.isCompletedToday) MaterialTheme.colorScheme.secondary
                                     else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (hwp.isCompletedToday) Icons.Default.Undo else Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (hwp.isCompletedToday) "Desmarcar" else "Marcar como completado hoy")
            }
        }
    }
}
