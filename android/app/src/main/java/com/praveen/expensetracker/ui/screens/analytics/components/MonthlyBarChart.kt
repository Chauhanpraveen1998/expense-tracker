package com.praveen.expensetracker.ui.screens.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.MonthlySpending
import com.praveen.expensetracker.ui.theme.CustomShapes
import com.praveen.expensetracker.ui.theme.ExpenseRed
import com.praveen.expensetracker.ui.theme.IncomeGreen
import com.praveen.expensetracker.ui.theme.Spacing
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun MonthlyBarChart(
    monthlySpending: List<MonthlySpending>,
    modifier: Modifier = Modifier
) {
    if (monthlySpending.isEmpty()) return

    val maxValue = remember(monthlySpending) {
        (monthlySpending.maxOfOrNull { maxOf(it.income, it.expense) } ?: 1.0).toFloat()
    }
    
    val monthLabels = monthlySpending.map { 
        it.month.format(DateTimeFormatter.ofPattern("MMM"))
    }

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
            Text(
                text = "Monthly Comparison",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val barWidth = size.width / (monthlySpending.size * 3)
                val spacing = barWidth / 2
                val maxHeight = size.height - 40f
                
                monthlySpending.forEachIndexed { index, spending ->
                    val x = index * (barWidth * 2 + spacing * 2) + spacing
                    
                    val incomeHeight = (spending.income.toFloat() / maxValue) * maxHeight
                    val expenseHeight = (spending.expense.toFloat() / maxValue) * maxHeight
                    
                    drawRoundRect(
                        color = IncomeGreen,
                        topLeft = Offset(x, maxHeight - incomeHeight),
                        size = Size(barWidth, incomeHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    
                    drawRoundRect(
                        color = ExpenseRed,
                        topLeft = Offset(x + barWidth + 4f, maxHeight - expenseHeight),
                        size = Size(barWidth, expenseHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.small))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LegendItem(color = IncomeGreen, label = "Income")
                Spacer(modifier = Modifier.padding(horizontal = Spacing.medium))
                LegendItem(color = ExpenseRed, label = "Expense")
            }
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.padding(end = 4.dp).height(12.dp)) {
            drawRect(color = color, size = androidx.compose.ui.geometry.Size(24f, size.height))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
