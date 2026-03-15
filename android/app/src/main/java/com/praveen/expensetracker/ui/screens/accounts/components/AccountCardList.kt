package com.praveen.expensetracker.ui.screens.accounts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType
import com.praveen.expensetracker.ui.components.AccountCard
import com.praveen.expensetracker.ui.components.AccountType as UIAccountType
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun AccountCardList(
    accounts: List<Account>,
    onAccountClick: (Account) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = Spacing.screenPadding,
            vertical = Spacing.medium
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.default)
    ) {
        items(
            items = accounts,
            key = { it.id }
        ) { account ->
            AccountCard(
                accountName = account.name,
                accountNumber = account.lastFourDigits ?: "****",
                balance = account.balance,
                accountType = when (account.type) {
                    AccountType.BANK -> UIAccountType.BANK
                    AccountType.CREDIT_CARD -> UIAccountType.CREDIT_CARD
                    AccountType.WALLET -> UIAccountType.WALLET
                    AccountType.CASH -> UIAccountType.CASH
                },
                gradientColors = listOf(account.colorPrimary, account.colorSecondary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
