package com.praveen.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.praveen.expensetracker.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DateHeader(
    date: Date,
    modifier: Modifier = Modifier
) {
    val formattedDate = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(date)
    
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = formattedDate,
            style = Typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DateHeader(
    dateString: String,
    modifier: Modifier = Modifier
) {
    val date = parseDate(dateString)
    if (date != null) {
        DateHeader(date = date, modifier = modifier)
    } else {
        Text(
            text = dateString,
            style = Typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
    }
}

private fun parseDate(dateString: String): Date? {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        inputFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }
}
