package com.praveen.expensetracker.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.Insight
import com.praveen.expensetracker.domain.model.InsightType
import com.praveen.expensetracker.ui.theme.CustomShapes
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun SmartInsightsCarousel(
    insights: List<Insight>,
    modifier: Modifier = Modifier,
    onInsightClick: (Insight) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    val displayInsights = insights.ifEmpty { getDefaultInsights() }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Smart Insights",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = Spacing.screenPadding)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            contentPadding = PaddingValues(horizontal = Spacing.screenPadding)
        ) {
            items(
                items = displayInsights,
                key = { it.id }
            ) { insight ->
                InsightCard(insight = insight)
            }
        }
    }
}

@Composable
private fun InsightCard(
    insight: Insight,
    modifier: Modifier = Modifier
) {
    val (icon, gradientColors) = getInsightStyle(insight.type)
    
    Card(
        modifier = modifier
            .width(280.dp)
            .height(120.dp),
        shape = CustomShapes.Card,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = { }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    brush = Brush.linearGradient(colors = gradientColors)
                )
                .padding(Spacing.default)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing.small))
                    
                    Text(
                        text = insight.type.name.replace("_", " "),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun getInsightStyle(type: InsightType): Pair<ImageVector, List<Color>> {
    return when (type) {
        InsightType.TIP -> Pair(
            Icons.Default.Lightbulb,
            listOf(Color(0xFF059669), Color(0xFF10B981))
        )
        InsightType.WARNING -> Pair(
            Icons.Default.Warning,
            listOf(Color(0xFFD97706), Color(0xFFF59E0B))
        )
        InsightType.INFO -> Pair(
            Icons.Default.Info,
            listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
        )
        InsightType.TREND -> Pair(
            Icons.AutoMirrored.Filled.TrendingUp,
            listOf(Color(0xFF7C3AED), Color(0xFF8B5CF6))
        )
    }
}

private fun getDefaultInsights(): List<Insight> {
    return listOf(
        Insight(
            id = "default_1",
            type = InsightType.INFO,
            title = "Start Tracking",
            description = "Add your transactions to get personalized insights and manage your finances better."
        ),
        Insight(
            id = "default_2",
            type = InsightType.TIP,
            title = "Set a Budget",
            description = "Create a monthly budget to track your spending and save more."
        ),
        Insight(
            id = "default_3",
            type = InsightType.TREND,
            title = "Track Weekly",
            description = "Regularly log expenses to see spending patterns over time."
        )
    )
}
