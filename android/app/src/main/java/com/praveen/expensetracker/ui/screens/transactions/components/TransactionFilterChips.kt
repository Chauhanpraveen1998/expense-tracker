package com.praveen.expensetracker.ui.screens.transactions.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.ui.components.CategoryChip
import com.praveen.expensetracker.ui.screens.transactions.TransactionFilter
import com.praveen.expensetracker.ui.theme.ExpenseRed
import com.praveen.expensetracker.ui.theme.IncomeGreen
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun TransactionFilterChips(
    selectedFilter: TransactionFilter,
    selectedCategories: Set<Category>,
    onFilterSelected: (TransactionFilter) -> Unit,
    onCategoryToggled: (Category) -> Unit,
    modifier: Modifier = Modifier,
    showCategoryFilters: Boolean = true,
    onFilterIconClick: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenPadding),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            CategoryChip(
                label = "All",
                isSelected = selectedFilter is TransactionFilter.All,
                onClick = { onFilterSelected(TransactionFilter.All) }
            )
            
            CategoryChip(
                label = "Income",
                isSelected = selectedFilter is TransactionFilter.Income,
                chipColor = IncomeGreen,
                onClick = { onFilterSelected(TransactionFilter.Income) }
            )
            
            CategoryChip(
                label = "Expense",
                isSelected = selectedFilter is TransactionFilter.Expense,
                chipColor = ExpenseRed,
                onClick = { onFilterSelected(TransactionFilter.Expense) }
            )
        }
        
        if (showCategoryFilters && selectedCategories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.small))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                selectedCategories.forEach { category ->
                    CategoryChip(
                        label = category.displayName,
                        isSelected = true,
                        onClick = { onCategoryToggled(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickCategoryFilters(
    selectedCategories: Set<Category>,
    onCategoryToggled: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val commonCategories = listOf(
        Category.FOOD_DINING,
        Category.SHOPPING,
        Category.TRANSPORT,
        Category.ENTERTAINMENT,
        Category.BILLS_UTILITIES,
        Category.GROCERIES
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        commonCategories.forEach { category ->
            CategoryChip(
                label = category.displayName,
                isSelected = selectedCategories.contains(category),
                onClick = { onCategoryToggled(category) }
            )
        }
    }
}
