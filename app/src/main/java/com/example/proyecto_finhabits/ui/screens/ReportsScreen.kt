package com.example.proyecto_finhabits.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyecto_finhabits.data.entities.Category
import com.example.proyecto_finhabits.data.entities.TransactionType
import com.example.proyecto_finhabits.ui.components.EmptyState
import com.example.proyecto_finhabits.ui.components.FinHabitsTopBar
import com.example.proyecto_finhabits.ui.components.currencyFmt
import com.example.proyecto_finhabits.ui.navigation.Routes
import com.example.proyecto_finhabits.ui.theme.ExpenseRed
import com.example.proyecto_finhabits.ui.theme.IncomeGreen
import com.example.proyecto_finhabits.viewmodel.HabitViewModel
import com.example.proyecto_finhabits.viewmodel.ReportPeriod
import com.example.proyecto_finhabits.viewmodel.TransactionViewModel

val PIE_COLORS = listOf(
    Color(0xFF1B8A5A), Color(0xFF1565C0), Color(0xFFF57F17),
    Color(0xFFAD1457), Color(0xFF6A1B9A), Color(0xFF00838F), Color(0xFF558B2F)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    nav: NavHostController,
    transactionVM: TransactionViewModel,
    habitVM: HabitViewModel
) {
    val period by transactionVM.reportPeriod.collectAsState()
    val transactions by transactionVM.reportTransactions.collectAsState()
    val income by transactionVM.reportIncome.collectAsState()
    val expenses by transactionVM.reportExpenses.collectAsState()
    val habits by habitVM.habitsWithProgress.collectAsState()

    // Group expenses by category
    val expensesByCategory = remember(transactions) {
        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    Scaffold(
        topBar = { FinHabitsTopBar("Reportes 📊", navBack = { nav.popBackStack() }) },
        bottomBar = { BottomNav(nav, Routes.REPORTS) }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Period selector
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReportPeriod.values().forEach { p ->
                        FilterChip(
                            selected = period == p,
                            onClick = { transactionVM.setReportPeriod(p) },
                            label = { Text(p.label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Summary cards
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard("Ingresos", income, IncomeGreen, Modifier.weight(1f))
                    SummaryCard("Gastos", expenses, ExpenseRed, Modifier.weight(1f))
                    SummaryCard("Saldo", income - expenses,
                        if (income >= expenses) IncomeGreen else ExpenseRed, Modifier.weight(1f))
                }
            }

            // Pie chart
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Gastos por categoría", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        if (expensesByCategory.isEmpty()) {
                            EmptyState("Sin gastos en este periodo", "📭")
                        } else {
                            PieChart(data = expensesByCategory, total = expenses)
                            Spacer(Modifier.height(12.dp))
                            expensesByCategory.forEachIndexed { i, (cat, amount) ->
                                PieLegendItem(
                                    color = PIE_COLORS[i % PIE_COLORS.size],
                                    label = "${cat.emoji} ${cat.label}",
                                    amount = amount,
                                    pct = if (expenses > 0) (amount / expenses * 100).toInt() else 0
                                )
                            }
                        }
                    }
                }
            }

            // Bar chart (category totals)
            if (expensesByCategory.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Comparativa por categoría", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(12.dp))
                            BarChart(data = expensesByCategory, maxValue = expensesByCategory.maxOf { it.second })
                        }
                    }
                }
            }

            // Habits summary
            if (habits.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Progreso de hábitos (semana)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(8.dp))
                            habits.forEach { hwp ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(hwp.habit.name, Modifier.weight(1f), fontSize = 14.sp)
                                    Text("${(hwp.weeklyRate * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = if (hwp.weeklyRate >= 0.7f) IncomeGreen else ExpenseRed)
                                }
                                LinearProgressIndicator(
                                    progress = { hwp.weeklyRate },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).padding(bottom = 4.dp),
                                    color = if (hwp.weeklyRate >= 0.7f) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SummaryCard(label: String, amount: Double, color: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(currencyFmt.format(amount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun PieChart(data: List<Pair<Category, Double>>, total: Double) {
    val angles = data.map { (_, v) -> (v / total * 360f).toFloat() }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = -90f
            angles.forEachIndexed { i, sweep ->
                drawArc(
                    color = PIE_COLORS[i % PIE_COLORS.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(20f, 20f),
                    size = Size(size.width - 40f, size.height - 40f),
                    style = Stroke(width = 55f)
                )
                startAngle += sweep
            }
        }
    }
}

@Composable
fun PieLegendItem(color: Color, label: String, amount: Double, pct: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(label, Modifier.weight(1f), fontSize = 13.sp)
        Text(currencyFmt.format(amount), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(6.dp))
        Text("$pct%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun BarChart(data: List<Pair<Category, Double>>, maxValue: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.forEachIndexed { i, (cat, amount) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("${cat.emoji}", fontSize = 16.sp, modifier = Modifier.width(28.dp))
                Column(Modifier.weight(1f)) {
                    Text(cat.label, fontSize = 12.sp)
                    LinearProgressIndicator(
                        progress = { if (maxValue > 0) (amount / maxValue).toFloat() else 0f },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = PIE_COLORS[i % PIE_COLORS.size],
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(currencyFmt.format(amount), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
            }
        }
    }
}
