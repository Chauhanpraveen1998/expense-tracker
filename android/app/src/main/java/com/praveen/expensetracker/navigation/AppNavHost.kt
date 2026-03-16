package com.praveen.expensetracker.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.praveen.expensetracker.ui.screens.accounts.AccountsScreen
import com.praveen.expensetracker.ui.screens.add.AddTransactionScreen
import com.praveen.expensetracker.ui.screens.analytics.AnalyticsScreen
import com.praveen.expensetracker.ui.screens.auth.LoginScreen
import com.praveen.expensetracker.ui.screens.auth.RegisterScreen
import com.praveen.expensetracker.ui.screens.auth.SplashScreen
import com.praveen.expensetracker.ui.screens.dashboard.DashboardScreen
import com.praveen.expensetracker.ui.screens.settings.ProfileScreen
import com.praveen.expensetracker.ui.screens.settings.SettingsScreen
import com.praveen.expensetracker.ui.screens.transactions.TransactionDetailScreen
import com.praveen.expensetracker.ui.screens.transactions.TransactionsScreen

private const val ANIMATION_DURATION = 300

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(ANIMATION_DURATION)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIMATION_DURATION)
                )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(ANIMATION_DURATION)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIMATION_DURATION)
                )
        }
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.Transactions.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToTransactionDetail = { transactionId ->
                    navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                }
            )
        }
        
        composable(route = Screen.Transactions.route) {
            TransactionsScreen(
                onNavigateToTransactionDetail = { transactionId ->
                    navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                }
            )
        }
        
        composable(route = Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTransactionAdded = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = Screen.Analytics.route) {
            AnalyticsScreen()
        }
        
        composable(route = Screen.Accounts.route) {
            AccountsScreen()
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument(Screen.TransactionDetail.ARG_TRANSACTION_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments
                ?.getString(Screen.TransactionDetail.ARG_TRANSACTION_ID) ?: ""
            TransactionDetailScreen(
                transactionId = transactionId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.AccountDetail.route,
            arguments = listOf(
                navArgument(Screen.AccountDetail.ARG_ACCOUNT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments
                ?.getString(Screen.AccountDetail.ARG_ACCOUNT_ID) ?: ""
            TransactionDetailScreen(
                transactionId = accountId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
