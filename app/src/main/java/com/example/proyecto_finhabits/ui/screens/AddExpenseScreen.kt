package com.example.proyecto_finhabits.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyecto_finhabits.ui.components.FinHabitsTopBar
import com.example.proyecto_finhabits.data.entities.Category
import com.example.proyecto_finhabits.data.entities.Transaction
import com.example.proyecto_finhabits.data.entities.TransactionType
import com.example.proyecto_finhabits.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    nav: NavHostController,
    vm: TransactionViewModel,
    editTransaction: Transaction? = null,
    editTransactionId: Long? = null
) {
    val allTx by vm.allTransactions.collectAsState()
    val existing = remember(editTransactionId, allTx) {
        if (editTransactionId != null) allTx.find { it.id == editTransactionId } else editTransaction
    }
    val isEdit = existing != null

    var amountText by remember { mutableStateOf(existing?.amount?.toString() ?: "") }
    var type by remember { mutableStateOf(existing?.type ?: TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(existing?.category ?: Category.FOOD) }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var date by remember { mutableStateOf(existing?.date ?: System.currentTimeMillis()) }
    var amountError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(saved) {
        if (saved) {
            snackbarHostState.showSnackbar(if (isEdit) "Movimiento actualizado ✓" else "Gasto guardado ✓")
            nav.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            FinHabitsTopBar(
                title = if (isEdit) "Editar movimiento" else "Registrar movimiento",
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
            // Type selector
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(Modifier.padding(4.dp)) {
                    TransactionType.values().forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(if (t == TransactionType.INCOME) "💰 Ingreso" else "💸 Gasto") },
                            modifier = Modifier.weight(1f).padding(4.dp)
                        )
                    }
                }
            }

            // Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it; amountError = false },
                label = { Text("Monto") },
                prefix = { Text("$") },
                isError = amountError,
                supportingText = { if (amountError) Text("Ingresa un monto válido mayor a 0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category
            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            CategorySelector(selected = category, onSelect = { category = it })

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Date
            val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))
            OutlinedTextField(
                value = dateFmt.format(Date(date)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha") },
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) { Text("Cambiar") }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) { amountError = true; return@Button }
                    if (isEdit && existing != null) {
                        vm.update(existing.copy(amount = amount, type = type, category = category, description = description, date = date))
                    } else {
                        vm.insert(amount, type, category, description, date)
                    }
                    saved = true
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(if (isEdit) "Actualizar" else "Guardar movimiento")
            }
        }
    }

    // Delete confirmation
    if (showDeleteDialog && existing != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar movimiento") },
            text = { Text("¿Estás seguro de que quieres eliminar este registro? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { vm.delete(existing); nav.popBackStack() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { date = it }
                    showDatePicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun CategorySelector(selected: Category, onSelect: (Category) -> Unit) {
    val rows = Category.values().toList().chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { cat ->
                    FilterChip(
                        selected = selected == cat,
                        onClick = { onSelect(cat) },
                        label = { Text("${cat.emoji} ${cat.label}", maxLines = 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space in last row
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
