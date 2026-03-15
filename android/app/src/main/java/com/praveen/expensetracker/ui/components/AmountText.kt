package com.praveen.expensetracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.praveen.expensetracker.ui.theme.ExpenseRed
import com.praveen.expensetracker.ui.theme.IncomeGreen

enum class AmountType {
    EXPENSE,
    INCOME,
    BALANCE
}

@Composable
fun AmountText(
    amount: Double,
    currencySymbol: String = "₹",
    type: AmountType = AmountType.EXPENSE,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    showSign: Boolean = true,
    compact: Boolean = false
) {
    val formattedAmount = if (compact) {
        formatCompactAmount(amount)
    } else {
        String.format("%.2f", amount)
    }

    val sign = when {
        !showSign -> ""
        type == AmountType.EXPENSE -> "-"
        type == AmountType.INCOME -> "+"
        else -> ""
    }

    val color = when (type) {
        AmountType.EXPENSE -> ExpenseRed
        AmountType.INCOME -> IncomeGreen
        AmountType.BALANCE -> MaterialTheme.colorScheme.onBackground
    }

    Text(
        text = "$sign$currencySymbol$formattedAmount",
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold
        ),
        color = color,
        modifier = modifier
    )
}

private fun formatCompactAmount(amount: Double): String {
    return when {
        amount >= 10_00_00_000 -> String.format("%.1fCr", amount / 10_00_00_000)
        amount >= 1_00_000 -> String.format("%.1fL", amount / 1_00_000)
        amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
        else -> String.format("%.0f", amount)
    }
}
