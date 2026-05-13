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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyecto_finhabits.ui.components.*
import com.example.proyecto_finhabits.ui.navigation.Routes
import com.example.proyecto_finhabits.viewmodel.HabitViewModel
import com.example.proyecto_finhabits.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavHostController,
    transactionVM: TransactionViewModel,
    habitVM: HabitViewModel
) {
    val balance by transactionVM.balance.collectAsState()
    val income by transactionVM.totalIncome.collectAsState()
    val expenses by transactionVM.totalExpenses.collectAsState()
    val latest by transactionVM.latestTransactions.collectAsState()
    val habitsProgress by habitVM.habitsWithProgress.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("FinHabits 💚", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Tu salud financiera", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { nav.navigate(Routes.REPORTS) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Reportes")
                    }
                    IconButton(onClick = { nav.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { nav.navigate(Routes.ADD_EXPENSE) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Registrar") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        bottomBar = { BottomNav(nav, Routes.HOME) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Balance card
            item { BalanceCard(balance, income, expenses) }

            // Quick actions
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.AddCircle,
                        label = "Gasto",
                        onClick = { nav.navigate(Routes.ADD_EXPENSE) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.CheckCircle,
                        label = "Hábitos",
                        onClick = { nav.navigate(Routes.HABITS) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.PieChart,
                        label = "Reportes",
                        onClick = { nav.navigate(Routes.REPORTS) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Active habits
            if (habitsProgress.isNotEmpty()) {
                item { SectionTitle("Hábitos activos") }
                items(habitsProgress.take(3)) { hwp ->
                    HabitProgressCard(
                        name = hwp.habit.name,
                        isCompleted = hwp.isCompletedToday,
                        weeklyRate = hwp.weeklyRate,
                        streak = hwp.habit.streak,
                        onToggle = { habitVM.toggleCompletion(hwp.habit) }
                    )
                }
            }

            // Recent transactions
            item { SectionTitle("Últimos movimientos") }
            if (latest.isEmpty()) {
                item { EmptyState("Aún no hay movimientos.\n¡Registra tu primer gasto!", "📊") }
            } else {
                items(latest) { t ->
                    TransactionItem(t, onClick = { nav.navigate(Routes.editExpense(t.id)) })
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun QuickActionCard(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun HabitProgressCard(
    name: String,
    isCompleted: Boolean,
    weeklyRate: Float,
    streak: Int,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer
                             else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (isCompleted) "✅" else "⭕", fontSize = 20.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Racha: $streak días 🔥", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onToggle) {
                    Text(if (isCompleted) "Deshacer" else "Completar", fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { weeklyRate },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                "${(weeklyRate * 100).toInt()}% esta semana",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun BottomNav(nav: NavHostController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { if (currentRoute != Routes.HOME) nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.HABITS,
            onClick = { if (currentRoute != Routes.HABITS) nav.navigate(Routes.HABITS) },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Hábitos") },
            label = { Text("Hábitos") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.REPORTS,
            onClick = { if (currentRoute != Routes.REPORTS) nav.navigate(Routes.REPORTS) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reportes") },
            label = { Text("Reportes") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.SETTINGS,
            onClick = { if (currentRoute != Routes.SETTINGS) nav.navigate(Routes.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
            label = { Text("Ajustes") }
        )
    }
}
