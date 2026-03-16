package com.praveen.expensetracker.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.praveen.expensetracker.ui.components.EmptyState
import com.praveen.expensetracker.ui.components.FinancialPulseRing
import com.praveen.expensetracker.ui.components.LoadingState
import com.praveen.expensetracker.ui.screens.dashboard.components.DashboardTopBar
import com.praveen.expensetracker.ui.screens.dashboard.components.RecentTransactionsList
import com.praveen.expensetracker.ui.screens.dashboard.components.SmartInsightsCarousel
import com.praveen.expensetracker.ui.screens.dashboard.components.SpendingTrendChart
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun DashboardScreen(
    onNavigateToAddExpense: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToTransactionDetail: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                LoadingState(
                    message = "Loading your finances...",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            uiState.error != null -> {
                EmptyState(
                    title = "Something went wrong",
                    description = uiState.error ?: "Please try again",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            else -> {
                DashboardContent(
                    uiState = uiState,
                    onSettingsClick = onNavigateToSettings,
                    onTransactionClick = { transaction ->
                        onNavigateToTransactionDetail(transaction.id)
                    },
                    onSeeAllClick = onNavigateToHistory
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onSettingsClick: () -> Unit,
    onTransactionClick: (com.praveen.expensetracker.domain.model.Transaction) -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Spacing.huge),
        verticalArrangement = Arrangement.spacedBy(Spacing.default)
    ) {
        item {
            DashboardTopBar(
                greeting = uiState.greeting,
                userName = uiState.userName,
                onSettingsClick = onSettingsClick
            )
        }
        
        item {
            FinancialPulseRing(
                spentAmount = uiState.totalSpent,
                budgetAmount = uiState.totalBudget,
                modifier = Modifier.padding(horizontal = Spacing.screenPadding)
            )
        }
        
        item {
            SmartInsightsCarousel(
                insights = uiState.insights,
                onInsightClick = { }
            )
        }
        
        item {
            if (uiState.spendingTrends.isNotEmpty()) {
                SpendingTrendChart(
                    trends = uiState.spendingTrends,
                    modifier = Modifier.padding(horizontal = Spacing.screenPadding)
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.small))
        }
        
        item {
            if (uiState.groupedTransactions.isNotEmpty()) {
                RecentTransactionsList(
                    groupedTransactions = uiState.groupedTransactions,
                    onTransactionClick = onTransactionClick,
                    onSeeAllClick = onSeeAllClick
                )
            } else {
                EmptyState(
                    title = "No transactions yet",
                    description = "Your transactions will appear here",
                    modifier = Modifier.padding(Spacing.screenPadding)
                )
            }
        }
    }
}
