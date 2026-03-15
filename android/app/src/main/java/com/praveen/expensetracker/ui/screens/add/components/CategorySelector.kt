package com.praveen.expensetracker.ui.screens.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectorBottomSheet(
    isVisible: Boolean,
    transactionType: TransactionType,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            CategorySelectorContent(
                transactionType = transactionType,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )
        }
    }
}

@Composable
fun CategorySelectorContent(
    transactionType: TransactionType,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = when (transactionType) {
        TransactionType.EXPENSE -> listOf(
            Category.FOOD_DINING,
            Category.SHOPPING,
            Category.TRANSPORT,
            Category.ENTERTAINMENT,
            Category.BILLS_UTILITIES,
            Category.HEALTH,
            Category.EDUCATION,
            Category.TRAVEL,
            Category.GROCERIES,
            Category.FUEL,
            Category.PERSONAL_CARE,
            Category.GIFTS,
            Category.TRANSFER,
            Category.OTHER_EXPENSE
        )
        TransactionType.INCOME -> listOf(
            Category.SALARY,
            Category.FREELANCE,
            Category.INVESTMENTS,
            Category.REFUND,
            Category.OTHER_INCOME
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.large)
    ) {
        Text(
            text = "Select Category",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = Spacing.screenPadding)
        )
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(horizontal = Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
        ) {
            items(categories) { category ->
                CategoryIcon(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.large))
    }
}
