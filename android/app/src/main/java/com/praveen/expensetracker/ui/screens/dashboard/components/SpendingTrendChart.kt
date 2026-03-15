package com.praveen.expensetracker.ui.screens.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.SpendingTrend
import com.praveen.expensetracker.ui.theme.CustomShapes
import com.praveen.expensetracker.ui.theme.PrimaryGreen
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun SpendingTrendChart(
    trends: List<SpendingTrend>,
    modifier: Modifier = Modifier,
    lineColor: Color = PrimaryGreen
) {
    if (trends.isEmpty()) return
    
    val maxAmount = remember(trends) { trends.maxOfOrNull { it.amount } ?: 1.0 }
    val minAmount = remember(trends) { trends.minOfOrNull { it.amount } ?: 0.0 }
    val range = (maxAmount - minAmount).coerceAtLeast(1.0)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CustomShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.default)
        ) {
            Text(
                text = "Spending Trend (Last 7 Days)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = Spacing.medium)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val padding = 16f
                    
                    val usableWidth = width - (padding * 2)
                    val usableHeight = height - (padding * 2)
                    
                    if (trends.size < 2) return@Canvas
                    
                    val stepX = usableWidth / (trends.size - 1)
                    
                    val points = trends.mapIndexed { index, trend ->
                        val x = padding + (index * stepX)
                        val y = padding + usableHeight - ((trend.amount - minAmount) / range * usableHeight).toFloat()
                        Offset(x, y)
                    }
                    
                    val fillPath = Path().apply {
                        moveTo(points.first().x, height - padding)
                        points.forEach { point ->
                            lineTo(point.x, point.y)
                        }
                        lineTo(points.last().x, height - padding)
                        close()
                    }
                    
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.3f),
                                lineColor.copy(alpha = 0.0f)
                            )
                        )
                    )
                    
                    val linePath = Path().apply {
                        points.forEachIndexed { index, point ->
                            if (index == 0) {
                                moveTo(point.x, point.y)
                            } else {
                                val prev = points[index - 1]
                                val midX = (prev.x + point.x) / 2
                                quadraticBezierTo(prev.x, prev.y, midX, (prev.y + point.y) / 2)
                                quadraticBezierTo(midX, (prev.y + point.y) / 2, point.x, point.y)
                            }
                        }
                    }
                    
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                    
                    points.forEach { point ->
                        drawCircle(
                            color = lineColor,
                            radius = 4f,
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2f,
                            center = point
                        )
                    }
                }
            }
        }
    }
}
