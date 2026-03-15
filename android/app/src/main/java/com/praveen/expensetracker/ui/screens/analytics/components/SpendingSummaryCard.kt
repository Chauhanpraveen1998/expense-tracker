package com.praveen.expensetracker.ui.screens.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.ui.components.AmountText
import com.praveen.expensetracker.ui.components.AmountType
import com.praveen.expensetracker.ui.theme.CustomShapes
import com.praveen.expensetracker.ui.theme.IncomeGreen
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun SpendingSummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    netSavings: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CustomShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.default)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Income",
                    amount = totalIncome,
                    type = AmountType.INCOME
                )
                
                SummaryItem(
                    label = "Expense",
                    amount = totalExpense,
                    type = AmountType.EXPENSE
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Savings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "₹${formatAmount(netSavings)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (netSavings >= 0) IncomeGreen else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    amount: Double,
    type: AmountType
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        AmountText(
            amount = amount,
            type = type,
            showSign = false,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
}

private fun formatAmount(amount: Double): String {
    val absAmount = kotlin.math.abs(amount)
    return when {
        absAmount >= 10_00_000 -> String.format("%.1fL", absAmount / 1_00_000)
        absAmount >= 1_000 -> String.format("%.1fK", absAmount / 1_000)
        else -> String.format("%.0f", absAmount)
    }
}
