package com.praveen.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.praveen.expensetracker.ui.screens.AddExpenseScreen
import com.praveen.expensetracker.ui.screens.DashboardScreen
import com.praveen.expensetracker.ui.screens.ExpenseHistoryScreen
import com.praveen.expensetracker.ui.screens.LoginScreen
import com.praveen.expensetracker.ui.screens.RegisterScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object AddExpense : Screen("add_expense")
    object ExpenseHistory : Screen("expense_history")
}

@Composable
fun ExpenseTrackerNavHost() {
    val navController = rememberNavController()
    val tokenManager: com.praveen.expensetracker.util.TokenManager = hiltViewModel<com.praveen.expensetracker.ui.screens.MainViewModel>().tokenManager
    val token by tokenManager.token.collectAsState(initial = null)
    val isLoggedIn = token != null

    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.ExpenseHistory.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExpenseAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ExpenseHistory.route + "?category={category}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            ExpenseHistoryScreen(
                initialCategory = category,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
