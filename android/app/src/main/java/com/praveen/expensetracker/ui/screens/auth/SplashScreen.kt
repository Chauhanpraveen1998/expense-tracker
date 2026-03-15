package com.praveen.expensetracker.ui.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.praveen.expensetracker.ui.theme.PrimaryGreen
import com.praveen.expensetracker.ui.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var startAnimation by remember { mutableFloatStateOf(0f) }
    val alphaAnim by animateFloatAsState(
        targetValue = startAnimation,
        animationSpec = tween(durationMillis = 1000),
        label = "splash_alpha"
    )
    
    LaunchedEffect(Unit) {
        startAnimation = 1f
        delay(1500)
    }
    
    LaunchedEffect(uiState.isCheckingAuth, uiState.isLoggedIn) {
        if (!uiState.isCheckingAuth) {
            delay(500)
            if (uiState.isLoggedIn) {
                onNavigateToDashboard()
            } else {
                onNavigateToLogin()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = PrimaryGreen
            )
            
            Spacer(modifier = Modifier.height(Spacing.default))
            
            Text(
                text = "Expense Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(Spacing.small))
            
            Text(
                text = "Manage your finances smartly",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(Spacing.xxLarge))
            
            if (uiState.isCheckingAuth) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PrimaryGreen,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
