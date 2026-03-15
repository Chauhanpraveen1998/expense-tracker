package com.praveen.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.ui.theme.ExpenseRed
import com.praveen.expensetracker.ui.theme.IncomeGreen
import com.praveen.expensetracker.ui.theme.Typography
import com.praveen.expensetracker.ui.theme.WarningYellow

enum class InsightType {
    POSITIVE,
    NEGATIVE,
    WARNING,
    INFO,
    TREND
}

@Composable
fun InsightCard(
    title: String,
    description: String,
    insightType: InsightType,
    percentage: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val (icon, iconColor) = when (insightType) {
        InsightType.POSITIVE, InsightType.TREND -> Pair(
            Icons.AutoMirrored.Filled.TrendingUp,
            IncomeGreen
        )
        InsightType.NEGATIVE -> Pair(
            Icons.AutoMirrored.Filled.TrendingDown,
            ExpenseRed
        )
        InsightType.WARNING -> Pair(
            Icons.Default.Warning,
            WarningYellow
        )
        InsightType.INFO -> Pair(
            Icons.Default.Info,
            IncomeGreen
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (percentage != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = percentage,
                            style = Typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = iconColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
