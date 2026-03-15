package com.praveen.expensetracker.ui.screens.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.ui.components.AmountText
import com.praveen.expensetracker.ui.components.AmountType
import com.praveen.expensetracker.ui.components.DateHeader
import com.praveen.expensetracker.ui.components.MerchantAvatar
import com.praveen.expensetracker.ui.components.SectionHeader
import com.praveen.expensetracker.ui.theme.Spacing
import com.praveen.expensetracker.ui.theme.Typography
import java.time.format.DateTimeFormatter

@Composable
fun RecentTransactionsList(
    groupedTransactions: Map<String, List<Transaction>>,
    modifier: Modifier = Modifier,
    onTransactionClick: (Transaction) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = "Recent Transactions",
            actionText = "See All",
            onActionClick = onSeeAllClick
        )
        
        groupedTransactions.forEach { (dateHeader, transactions) ->
            DateHeader(
                dateString = dateHeader,
                modifier = Modifier.padding(
                    horizontal = Spacing.screenPadding,
                    vertical = Spacing.small
                )
            )
            
            transactions.forEach { transaction ->
                TransactionListItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) },
                    modifier = Modifier.padding(
                        horizontal = Spacing.screenPadding,
                        vertical = Spacing.extraSmall
                    )
                )
            }
        }
    }
}

@Composable
private fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MerchantAvatar(
                    logoUrl = transaction.merchantLogoUrl,
                    merchantName = transaction.merchantName,
                    size = 48.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = transaction.merchantName,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.category.displayName,
                            style = Typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (transaction.isRecurring) {
                            Text(
                                text = " • Recurring",
                                style = Typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                AmountText(
                    amount = transaction.amount,
                    type = if (transaction.type == TransactionType.INCOME) AmountType.INCOME else AmountType.EXPENSE
                )
                Text(
                    text = transaction.dateTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    style = Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
