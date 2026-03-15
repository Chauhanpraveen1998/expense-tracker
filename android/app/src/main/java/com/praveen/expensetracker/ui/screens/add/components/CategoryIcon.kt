package com.praveen.expensetracker.ui.screens.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.ui.theme.Spacing

@Composable
fun CategoryIcon(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp
) {
    val (icon, color) = getCategoryIconAndColor(category)
    
    val backgroundColor = if (isSelected) {
        color.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val borderColor = if (isSelected) color else Color.Transparent

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(Spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.displayName,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun getCategoryIconAndColor(category: Category): Pair<ImageVector, Color> {
    return when (category) {
        Category.FOOD_DINING -> Icons.Default.Restaurant to Color(0xFFFF6B6B)
        Category.SHOPPING -> Icons.Default.ShoppingBag to Color(0xFF9B59B6)
        Category.TRANSPORT -> Icons.Default.DirectionsCar to Color(0xFF3498DB)
        Category.ENTERTAINMENT -> Icons.Default.Movie to Color(0xFFE91E63)
        Category.BILLS_UTILITIES -> Icons.Default.Receipt to Color(0xFF607D8B)
        Category.HEALTH -> Icons.Default.MedicalServices to Color(0xFF4CAF50)
        Category.EDUCATION -> Icons.Default.School to Color(0xFF2196F3)
        Category.TRAVEL -> Icons.Default.Flight to Color(0xFF00BCD4)
        Category.GROCERIES -> Icons.Default.LocalGroceryStore to Color(0xFF8BC34A)
        Category.FUEL -> Icons.Default.LocalGasStation to Color(0xFFFF9800)
        Category.PERSONAL_CARE -> Icons.Default.Spa to Color(0xFFE91E63)
        Category.GIFTS -> Icons.Default.CardGiftcard to Color(0xFFF44336)
        Category.INVESTMENTS -> Icons.Default.TrendingUp to Color(0xFF4CAF50)
        Category.SALARY -> Icons.Default.Payments to Color(0xFF4CAF50)
        Category.FREELANCE -> Icons.Default.Work to Color(0xFF00BCD4)
        Category.REFUND -> Icons.Default.Replay to Color(0xFF2196F3)
        Category.OTHER_INCOME -> Icons.Default.AttachMoney to Color(0xFF4CAF50)
        Category.OTHER_EXPENSE -> Icons.Default.Category to Color(0xFF9E9E9E)
        Category.TRANSFER -> Icons.Default.SwapHoriz to Color(0xFF607D8B)
    }
}
