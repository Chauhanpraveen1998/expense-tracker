package com.praveen.expensetracker.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.praveen.expensetracker.ui.screens.analytics.components.CategoryPieChart
import com.praveen.expensetracker.ui.screens.analytics.components.DateRangeSelector
import com.praveen.expensetracker.ui.screens.analytics.components.MonthlyBarChart
import com.praveen.expensetracker.ui.screens.analytics.components.SpendingSummaryCard
import com.praveen.expensetracker.ui.screens.analytics.components.TopMerchantsList
import com.praveen.expensetracker.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analytics",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.default),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error ?: "An error occurred",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                AnalyticsContent(
                    uiState = uiState,
                    onDateRangeSelected = { option ->
                        viewModel.onEvent(AnalyticsEvent.DateRangeSelected(option))
                    }
                )
            }
        }
    }
}

@Composable
private fun AnalyticsContent(
    uiState: AnalyticsUiState,
    onDateRangeSelected: (DateRangeOption) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(Spacing.default),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        item {
            DateRangeSelector(
                selectedOption = uiState.selectedDateRange,
                onOptionSelected = onDateRangeSelected
            )
        }

        item {
            SpendingSummaryCard(
                totalIncome = uiState.totalIncome,
                totalExpense = uiState.totalExpense,
                netSavings = uiState.netSavings
            )
        }

        item {
            CategoryPieChart(
                categorySpending = uiState.categoryBreakdown
            )
        }

        item {
            MonthlyBarChart(
                monthlySpending = uiState.monthlyComparison
            )
        }

        item {
            TopMerchantsList(
                merchants = uiState.topMerchants
            )
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xxLarge))
        }
    }
}
