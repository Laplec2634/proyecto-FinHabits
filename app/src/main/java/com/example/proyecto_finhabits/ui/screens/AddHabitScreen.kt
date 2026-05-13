package com.example.proyecto_finhabits.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyecto_finhabits.data.entities.HabitFrequency
import com.example.proyecto_finhabits.ui.components.FinHabitsTopBar
import com.example.proyecto_finhabits.viewmodel.HabitViewModel

@Composable
fun AddHabitScreen(
    nav: NavHostController,
    vm: HabitViewModel,
    editHabitId: Long? = null
) {
    val allHabits by vm.allHabits.collectAsState()
    val existing = remember(editHabitId, allHabits) {
        if (editHabitId != null) allHabits.find { it.id == editHabitId } else null
    }
    val isEdit = existing != null

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var frequency by remember { mutableStateOf(existing?.frequency ?: HabitFrequency.DAILY) }
    var nameError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(saved) {
        if (saved) {
            snackbarHostState.showSnackbar(if (isEdit) "Hábito actualizado ✓" else "Hábito creado ✓")
            nav.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            FinHabitsTopBar(
                title = if (isEdit) "Editar hábito" else "Nuevo hábito",
                navBack = { nav.popBackStack() },
                actions = {
                    if (isEdit) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text("Nombre del hábito") },
                placeholder = { Text("Ej: Registrar mis gastos diarios") },
                isError = nameError,
                supportingText = { if (nameError) Text("El nombre no puede estar vacío") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                placeholder = { Text("Ej: Anotar cada compra en la app") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Text("Frecuencia", style = MaterialTheme.typography.labelLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HabitFrequency.values().forEach { f ->
                    FilterChip(
                        selected = frequency == f,
                        onClick = { frequency = f },
                        label = { Text(f.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tips card
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(14.dp)) {
                    Text("💡 Consejos para buenos hábitos", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(6.dp))
                    Text("• Sé específico: \"Registrar cada gasto\" es mejor que \"Controlar dinero\"", style = MaterialTheme.typography.bodySmall)
                    Text("• Empieza con hábitos diarios pequeños", style = MaterialTheme.typography.bodySmall)
                    Text("• La constancia genera la racha 🔥", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (name.isBlank()) { nameError = true; return@Button }
                    if (isEdit && existing != null) {
                        vm.update(existing.copy(name = name.trim(), description = description.trim(), frequency = frequency))
                    } else {
                        vm.insert(name.trim(), description.trim(), frequency)
                    }
                    saved = true
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(if (isEdit) "Actualizar hábito" else "Crear hábito")
            }
        }
    }

    if (showDeleteDialog && existing != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar hábito") },
            text = { Text("¿Eliminar \"${existing.name}\"? Se perderá toda su racha y progreso.") },
            confirmButton = {
                TextButton(onClick = { vm.delete(existing); nav.popBackStack() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }
}
