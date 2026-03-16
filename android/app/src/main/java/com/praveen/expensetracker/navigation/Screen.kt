package com.praveen.expensetracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object AddTransaction : Screen("add_transaction")
    object Analytics : Screen("analytics")
    object Accounts : Screen("accounts")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    
    object TransactionDetail : Screen("transaction/{transactionId}") {
        fun createRoute(transactionId: String): String = "transaction/$transactionId"
        const val ARG_TRANSACTION_ID = "transactionId"
    }
    
    object AccountDetail : Screen("account/{accountId}") {
        fun createRoute(accountId: String): String = "account/$accountId"
        const val ARG_ACCOUNT_ID = "accountId"
    }
}

enum class BottomNavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean = false,
    val badgeCount: Int? = null
) {
    HOME(
        screen = Screen.Dashboard,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    TRANSACTIONS(
        screen = Screen.Transactions,
        title = "History",
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt
    ),
    ADD(
        screen = Screen.AddTransaction,
        title = "Add",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add
    ),
    ANALYTICS(
        screen = Screen.Analytics,
        title = "Stats",
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Outlined.Analytics
    ),
    ACCOUNTS(
        screen = Screen.Accounts,
        title = "Accounts",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet
    )
}
