package com.praveen.expensetracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.data.model.DailyExpense
import com.praveen.expensetracker.data.model.Expense
import com.praveen.expensetracker.data.repository.AuthRepository
import com.praveen.expensetracker.data.repository.BudgetRepository
import com.praveen.expensetracker.data.repository.ExpenseRepository
import com.praveen.expensetracker.data.repository.NetworkErrorCodes
import com.praveen.expensetracker.data.repository.Result
import com.praveen.expensetracker.ui.components.ExpenseItem
import com.praveen.expensetracker.ui.theme.Typography
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

data class DashboardUiState(
    val totalSpentThisMonth: Double = 0.0,
    val recentExpenses: List<Expense> = emptyList(),
    val dailyExpenses: List<DailyExpense> = emptyList(),
    val budgetStatusList: List<com.praveen.expensetracker.data.model.BudgetStatus> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUnauthorized: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val monthlyTotalResult = expenseRepository.getMonthlyTotal()
            val recentExpensesResult = expenseRepository.getRecentExpenses(5)
            val dailyExpensesResult = expenseRepository.getDailyExpenses(7)
            val budgetStatusResult = budgetRepository.getBudgetStatus()

            when (monthlyTotalResult) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(totalSpentThisMonth = monthlyTotalResult.data)
                }
                is Result.Error -> {
                    if (monthlyTotalResult.code == NetworkErrorCodes.UNAUTHORIZED) {
                        _uiState.value = _uiState.value.copy(isUnauthorized = true)
                        authRepository.logout()
                        return@launch
                    }
                }
            }

            when (recentExpensesResult) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(recentExpenses = recentExpensesResult.data)
                }
                is Result.Error -> {
                    if (recentExpensesResult.code == NetworkErrorCodes.UNAUTHORIZED) {
                        _uiState.value = _uiState.value.copy(isUnauthorized = true)
                        authRepository.logout()
                        return@launch
                    }
                }
            }

            when (dailyExpensesResult) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(dailyExpenses = dailyExpensesResult.data)
                }
                else -> {}
            }

            when (budgetStatusResult) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(budgetStatusList = budgetStatusResult.data)
                }
                else -> {}
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedItem by remember { mutableStateOf(0) }

    LaunchedEffect(uiState.isUnauthorized) {
        if (uiState.isUnauthorized) {
            viewModel.logout()
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White) },
                actions = {
                    IconButton(onClick = { viewModel.loadDashboard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        viewModel.loadDashboard()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add Expense", modifier = Modifier.size(48.dp)) },
                    label = { Text("Add") },
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        onNavigateToAddExpense()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    selected = selectedItem == 2,
                    onClick = {
                        selectedItem = 2
                        onNavigateToHistory()
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn {
                item {
                    TotalExpenseCard(totalSpent = uiState.totalSpentThisMonth)
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                if (uiState.dailyExpenses.isNotEmpty()) {
                    item {
                        SpendFrequencyChart(dailyExpenses = uiState.dailyExpenses)
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                if (uiState.budgetStatusList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Budget Status",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.budgetStatusList.take(3).forEach { budget ->
                                BudgetStatusChip(budget = budget, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                item {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.recentExpenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No expenses yet", color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = onNavigateToAddExpense) {
                                    Text("Add your first expense")
                                }
                            }
                        }
                    }
                } else {
                    items(uiState.recentExpenses) { expense ->
                        ExpenseItem(expense = expense)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TotalExpenseCard(totalSpent: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Total This Month",
                style = Typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${String.format("%.2f", totalSpent)}",
                style = Typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun SpendFrequencyChart(dailyExpenses: List<DailyExpense>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Last 7 Days",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (dailyExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No data available", color = Color.Gray)
            }
        } else {
            val maxAmount = dailyExpenses.maxOfOrNull { it.amount } ?: 1.0
            val primaryColor = MaterialTheme.colorScheme.primary
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        val barWidth = size.width / (dailyExpenses.size * 2)
                        val maxHeight = size.height
                        
                        dailyExpenses.forEachIndexed { index, expense ->
                            val barHeight = if (maxAmount > 0) {
                                (expense.amount / maxAmount * maxHeight * 0.8).toFloat()
                            } else 0f
                            
                            drawRoundRect(
                                color = primaryColor,
                                topLeft = Offset(index * barWidth * 2 + barWidth / 2, maxHeight - barHeight),
                                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dailyExpenses.forEach { expense ->
                            val day = try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
                                val date = inputFormat.parse(expense.date)
                                outputFormat.format(date ?: Date())
                            } catch (e: Exception) {
                                expense.date.takeLast(2)
                            }
                            Text(day, style = Typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetStatusChip(budget: com.praveen.expensetracker.data.model.BudgetStatus, modifier: Modifier = Modifier) {
    val backgroundColor = if (budget.isOverBudget) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val textColor = if (budget.isOverBudget) Color(0xFFD32F2F) else Color(0xFF388E3C)
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = budget.category,
                style = Typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "$${String.format("%.0f", budget.spentAmount)} / $${String.format("%.0f", budget.budgetAmount)}",
                style = Typography.labelSmall,
                color = textColor
            )
        }
    }
}
