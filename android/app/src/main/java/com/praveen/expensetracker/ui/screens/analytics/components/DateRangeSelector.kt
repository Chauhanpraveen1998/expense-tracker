package com.praveen.expensetracker.ui.screens.analytics.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.ui.components.CategoryChip
import com.praveen.expensetracker.ui.screens.analytics.DateRangeOption
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun DateRangeSelector(
    selectedOption: DateRangeOption,
    onOptionSelected: (DateRangeOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        DateRangeOption.entries.filter { it != DateRangeOption.CUSTOM }.forEach { option ->
            CategoryChip(
                label = option.label,
                isSelected = selectedOption == option,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}
