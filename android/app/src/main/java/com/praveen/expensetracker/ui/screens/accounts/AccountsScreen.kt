package com.praveen.expensetracker.ui.screens.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.praveen.expensetracker.ui.components.EmptyState
import com.praveen.expensetracker.ui.components.LoadingState
import com.praveen.expensetracker.ui.screens.accounts.components.AccountCardList
import com.praveen.expensetracker.ui.screens.accounts.components.AddAccountDialog
import com.praveen.expensetracker.ui.screens.accounts.components.TotalBalanceCard
import com.praveen.expensetracker.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Accounts",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AccountsEvent.ShowAddAccountDialog) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Account",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState(
                        message = "Loading accounts...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    EmptyState(
                        title = "Something went wrong",
                        description = uiState.error ?: "Please try again",
                        actionText = "Retry",
                        onActionClick = { viewModel.onEvent(AccountsEvent.Refresh) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.accounts.isEmpty() -> {
                    EmptyState(
                        title = "No accounts yet",
                        description = "Add your bank accounts, credit cards, and wallets to track balances",
                        actionText = "Add Account",
                        onActionClick = { viewModel.onEvent(AccountsEvent.ShowAddAccountDialog) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TotalBalanceCard(
                            totalBalance = uiState.totalBalance,
                            accountCount = uiState.accounts.size,
                            modifier = Modifier.padding(
                                horizontal = Spacing.screenPadding,
                                vertical = Spacing.medium
                            )
                        )

                        AccountCardList(
                            accounts = uiState.accounts,
                            onAccountClick = { account ->
                                viewModel.onEvent(AccountsEvent.EditAccount(account))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (uiState.showAddAccountDialog) {
            AddAccountDialog(
                editingAccount = uiState.editingAccount,
                onDismiss = { viewModel.onEvent(AccountsEvent.DismissAddAccountDialog) },
                onSave = { state -> viewModel.onEvent(AccountsEvent.SaveAccount(state)) }
            )
        }
    }
}
