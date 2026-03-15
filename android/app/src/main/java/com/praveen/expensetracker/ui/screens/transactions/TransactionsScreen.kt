package com.praveen.expensetracker.ui.screens.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.praveen.expensetracker.ui.components.EmptyState
import com.praveen.expensetracker.ui.components.LoadingState
import com.praveen.expensetracker.ui.screens.transactions.components.QuickCategoryFilters
import com.praveen.expensetracker.ui.screens.transactions.components.TransactionFilterChips
import com.praveen.expensetracker.ui.screens.transactions.components.TransactionSearchBar
import com.praveen.expensetracker.ui.screens.transactions.components.TransactionSummaryCard
import com.praveen.expensetracker.ui.screens.transactions.components.TransactionTimeline
import com.praveen.expensetracker.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onNavigateToTransactionDetail: (String) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCategoryFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState(
                        message = "Loading transactions...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    EmptyState(
                        title = "Something went wrong",
                        description = uiState.error ?: "Please try again",
                        actionText = "Retry",
                        onActionClick = { viewModel.onEvent(TransactionsEvent.Refresh) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TransactionSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = { 
                                viewModel.onEvent(TransactionsEvent.SearchQueryChanged(it))
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        TransactionFilterChips(
                            selectedFilter = uiState.selectedFilter,
                            selectedCategories = uiState.selectedCategories,
                            onFilterSelected = { 
                                viewModel.onEvent(TransactionsEvent.FilterSelected(it))
                            },
                            onCategoryToggled = { 
                                viewModel.onEvent(TransactionsEvent.CategoryToggled(it))
                            },
                            onFilterIconClick = { showCategoryFilters = !showCategoryFilters }
                        )
                        
                        AnimatedVisibility(
                            visible = showCategoryFilters,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(Spacing.small))
                                QuickCategoryFilters(
                                    selectedCategories = uiState.selectedCategories,
                                    onCategoryToggled = {
                                        viewModel.onEvent(TransactionsEvent.CategoryToggled(it))
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        TransactionSummaryCard(
                            totalIncome = uiState.totalIncome,
                            totalExpense = uiState.totalExpense,
                            transactionCount = uiState.filteredTransactions.size
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        if (uiState.filteredTransactions.isEmpty()) {
                            val title = if (uiState.searchQuery.isNotBlank() || 
                                       uiState.selectedFilter !is TransactionFilter.All ||
                                       uiState.selectedCategories.isNotEmpty()) {
                                "No matching transactions"
                            } else {
                                "No transactions yet"
                            }
                            val description = if (uiState.searchQuery.isNotBlank()) {
                                "Try a different search term"
                            } else if (uiState.selectedFilter !is TransactionFilter.All ||
                                       uiState.selectedCategories.isNotEmpty()) {
                                "Try clearing filters"
                            } else {
                                "Your transactions will appear here"
                            }
                            val actionText = if (uiState.searchQuery.isNotBlank() || 
                                                uiState.selectedFilter !is TransactionFilter.All ||
                                                uiState.selectedCategories.isNotEmpty()) {
                                "Clear Filters"
                            } else null
                            
                            EmptyState(
                                title = title,
                                description = description,
                                icon = Icons.Default.Receipt,
                                actionText = actionText,
                                onActionClick = {
                                    viewModel.onEvent(TransactionsEvent.ClearFilters)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterHorizontally)
                            )
                        } else {
                            TransactionTimeline(
                                groupedTransactions = uiState.groupedTransactions,
                                onTransactionClick = { transaction ->
                                    onNavigateToTransactionDetail(transaction.id)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
