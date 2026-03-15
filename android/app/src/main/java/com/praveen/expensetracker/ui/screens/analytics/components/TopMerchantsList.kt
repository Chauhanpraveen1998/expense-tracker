package com.praveen.expensetracker.ui.screens.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.MerchantSpending
import com.praveen.expensetracker.ui.components.AmountText
import com.praveen.expensetracker.ui.components.AmountType
import com.praveen.expensetracker.ui.theme.CustomShapes
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun TopMerchantsList(
    merchants: List<MerchantSpending>,
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
            Text(
                text = "Top Merchants",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            if (merchants.isEmpty()) {
                Text(
                    text = "No merchant data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                merchants.take(5).forEachIndexed { index, merchant ->
                    MerchantItem(
                        rank = index + 1,
                        merchant = merchant
                    )
                    if (index < minOf(4, merchants.size - 1)) {
                        Spacer(modifier = Modifier.height(Spacing.small))
                    }
                }
            }
        }
    }
}

@Composable
private fun MerchantItem(
    rank: Int,
    merchant: MerchantSpending
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = merchant.merchantName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        AmountText(
            amount = merchant.totalAmount,
            type = AmountType.EXPENSE,
            showSign = false,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    }
}
