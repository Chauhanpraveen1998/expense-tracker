package com.praveen.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.ui.theme.ExpenseRed
import com.praveen.expensetracker.ui.theme.IncomeGreen
import com.praveen.expensetracker.ui.theme.PrimaryGreen
import com.praveen.expensetracker.ui.theme.RingBackground
import com.praveen.expensetracker.ui.theme.WarningYellow

@Composable
fun FinancialPulseRing(
    spentAmount: Double,
    budgetAmount: Double,
    currencySymbol: String = "₹",
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 24.dp,
    animationDuration: Int = 1000
) {
    val progress = if (budgetAmount > 0) {
        (spentAmount / budgetAmount).toFloat().coerceIn(0f, 1.5f)
    } else 0f
    
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(progress) {
        animatedProgress = progress
    }
    
    val animatedProgressValue by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = animationDuration),
        label = "ring_progress"
    )
    
    val progressColor = when {
        progress > 1f -> ExpenseRed
        progress > 0.8f -> WarningYellow
        else -> PrimaryGreen
    }
    
    val safeToSpend = (budgetAmount - spentAmount).coerceAtLeast(0.0)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (this.size.minDimension - strokeWidthPx) / 2
            val topLeft = Offset(
                (this.size.width - 2 * radius) / 2,
                (this.size.height - 2 * radius) / 2
            )
            val arcSize = Size(radius * 2, radius * 2)
            
            drawArc(
                color = RingBackground,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
            
            val sweepAngle = (360f * animatedProgressValue).coerceAtMost(360f)
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
            
            if (animatedProgressValue > 1f) {
                val overflowSweep = 360f * (animatedProgressValue - 1f)
                drawArc(
                    color = ExpenseRed.copy(alpha = 0.5f),
                    startAngle = -90f,
                    sweepAngle = overflowSweep.coerceAtMost(360f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx * 0.5f, cap = StrokeCap.Round)
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (safeToSpend > 0) "Safe to Spend" else "Over Budget",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "$currencySymbol${formatAmount(safeToSpend)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = if (safeToSpend > 0) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    ExpenseRed
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "of $currencySymbol${formatAmount(budgetAmount)} budget",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    return when {
        amount >= 10_00_00_000 -> String.format("%.1fCr", amount / 10_00_00_000)
        amount >= 1_00_000 -> String.format("%.1fL", amount / 1_00_000)
        amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
        else -> String.format("%.0f", amount)
    }
}
