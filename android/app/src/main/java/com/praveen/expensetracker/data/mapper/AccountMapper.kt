package com.praveen.expensetracker.data.mapper

import androidx.compose.ui.graphics.Color
import com.praveen.expensetracker.data.local.entity.AccountEntity
import com.praveen.expensetracker.data.local.entity.SyncStatus
import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType
import java.time.LocalDateTime

fun Account.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        type = type.name,
        balance = balance,
        bankName = bankName,
        lastFourDigits = lastFourDigits,
        colorPrimary = colorToHex(colorPrimary),
        colorSecondary = colorToHex(colorSecondary),
        isActive = isActive,
        syncStatus = syncStatus,
        lastModified = LocalDateTime.now()
    )
}

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        type = try { AccountType.valueOf(type) } catch (e: Exception) { AccountType.BANK },
        balance = balance,
        bankName = bankName,
        lastFourDigits = lastFourDigits,
        colorPrimary = hexToColor(colorPrimary, Color(0xFF1E3A8A)),
        colorSecondary = hexToColor(colorSecondary, Color(0xFF3B82F6)),
        isActive = isActive
    )
}

private fun colorToHex(color: Color): String {
    val argb = android.graphics.Color.argb(
        (color.alpha * 255).toInt(),
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )
    return String.format("#%06X", 0xFFFFFF and argb)
}

private fun hexToColor(hex: String?, default: Color): Color {
    if (hex.isNullOrBlank()) return default
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        default
    }
}
