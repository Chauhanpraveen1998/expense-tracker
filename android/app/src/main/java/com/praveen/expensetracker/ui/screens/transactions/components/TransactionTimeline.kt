package com.praveen.expensetracker.ui.screens.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType as DomainTransactionType
import com.praveen.expensetracker.ui.components.AmountType
import com.praveen.expensetracker.ui.components.DateHeader
import com.praveen.expensetracker.ui.components.TransactionItem
import com.praveen.expensetracker.ui.theme.Spacing
import java.time.format.DateTimeFormatter

@Composable
fun TransactionTimeline(
    groupedTransactions: Map<String, List<Transaction>>,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(bottom = Spacing.huge)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
    ) {
        groupedTransactions.forEach { (dateHeader, transactions) ->
            item(key = "header_$dateHeader") {
                DateHeader(
                    dateString = dateHeader,
                    modifier = Modifier.padding(
                        horizontal = Spacing.screenPadding,
                        vertical = Spacing.small
                    )
                )
            }
            
            items(
                items = transactions,
                key = { it.id }
            ) { transaction ->
                TransactionItem(
                    merchantName = transaction.merchantName,
                    category = transaction.category.displayName,
                    amount = transaction.amount,
                    type = when (transaction.type) {
                        DomainTransactionType.INCOME -> AmountType.INCOME
                        DomainTransactionType.EXPENSE -> AmountType.EXPENSE
                    },
                    timeString = transaction.dateTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    merchantLogoUrl = transaction.merchantLogoUrl,
                    note = transaction.note,
                    onClick = { onTransactionClick(transaction) },
                    modifier = Modifier.padding(horizontal = Spacing.screenPadding)
                )
            }
        }
    }
}
