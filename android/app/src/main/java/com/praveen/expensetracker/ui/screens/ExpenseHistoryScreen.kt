package com.praveen.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.data.model.Category
import com.praveen.expensetracker.data.model.Expense
import com.praveen.expensetracker.data.repository.CategoryRepository
import com.praveen.expensetracker.data.repository.ExpenseRepository
import com.praveen.expensetracker.data.repository.Result
import com.praveen.expensetracker.ui.components.ExpenseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExpenseHistoryUiState(
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedCategoryName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class ExpenseHistoryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseHistoryUiState())
    val uiState: StateFlow<ExpenseHistoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadExpenses()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = categoryRepository.getCategories()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(categories = result.data)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(categories = getDefaultCategories())
                }
            }
        }
    }

    fun loadExpenses(categoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true, 
                isRefreshing = true,
                selectedCategoryId = categoryId,
                error = null
            )
            
            val result = if (categoryId != null && categoryId.isNotEmpty()) {
                expenseRepository.getExpensesByCategory(categoryId)
            } else {
                expenseRepository.getExpenses()
            }

            when (result) {
                is Result.Success -> {
                    val categoryName = if (categoryId != null) {
                        _uiState.value.categories.find { it.id == categoryId }?.name
                    } else null
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        expenses = result.data,
                        selectedCategoryName = categoryName
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun refresh() {
        loadExpenses(_uiState.value.selectedCategoryId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun getDefaultCategories(): List<Category> {
        return listOf(
            Category("1", "Food"),
            Category("2", "Transport"),
            Category("3", "Shopping"),
            Category("4", "Entertainment"),
            Category("5", "Bills"),
            Category("6", "Health"),
            Category("7", "Other")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHistoryScreen(
    initialCategory: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ExpenseHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            viewModel.loadExpenses(initialCategory)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense History", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
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

            if (uiState.selectedCategoryName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    AssistChip(
                        onClick = { viewModel.loadExpenses(null) },
                        label = { Text("Filter: ${uiState.selectedCategoryName}") },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.loadExpenses(null) }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear filter", modifier = Modifier.size(16.dp))
                            }
                        }
                    )
                }
            }

            if (uiState.isLoading && !uiState.isRefreshing) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No expenses found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        if (uiState.selectedCategoryId != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.loadExpenses(null) }) {
                                Text("Clear filter")
                            }
                        }
                    }
                }
            } else {
                LazyColumn {
                    items(uiState.expenses) { expense ->
                        ExpenseItem(expense = expense)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Filter by Category",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ListItem(
                        headlineContent = { Text("All Categories", color = Color.White) },
                        leadingContent = {
                            RadioButton(
                                selected = uiState.selectedCategoryId == null,
                                onClick = {
                                    viewModel.loadExpenses(null)
                                    showFilterSheet = false
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val categoriesToShow = if (uiState.categories.isEmpty()) getDefaultCategories() else uiState.categories
                    categoriesToShow.forEach { category ->
                        ListItem(
                            headlineContent = { Text(category.name, color = Color.White) },
                            leadingContent = {
                                RadioButton(
                                    selected = uiState.selectedCategoryId == category.id,
                                    onClick = {
                                        viewModel.loadExpenses(category.id)
                                        showFilterSheet = false
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

private fun getDefaultCategories(): List<Category> {
    return listOf(
        Category("1", "Food"),
        Category("2", "Transport"),
        Category("3", "Shopping"),
        Category("4", "Entertainment"),
        Category("5", "Bills"),
        Category("6", "Health"),
        Category("7", "Other")
    )
}
