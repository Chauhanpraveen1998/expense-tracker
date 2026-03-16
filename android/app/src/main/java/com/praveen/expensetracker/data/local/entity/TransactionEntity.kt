package com.praveen.expensetracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["date_time"]),
        Index(value = ["category"]),
        Index(value = ["type"]),
        Index(value = ["merchant_name"]),
        Index(value = ["sync_status"])
    ]
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "merchant_name")
    val merchantName: String,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "type")
    val type: String,
    
    @ColumnInfo(name = "date_time")
    val dateTime: LocalDateTime,
    
    @ColumnInfo(name = "account_id")
    val accountId: String? = null,
    
    @ColumnInfo(name = "note")
    val note: String? = null,
    
    @ColumnInfo(name = "merchant_logo_url")
    val merchantLogoUrl: String? = null,
    
    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = false,
    
    @ColumnInfo(name = "tags")
    val tags: String? = null,
    
    @ColumnInfo(name = "sms_hash")
    val smsHash: String? = null,
    
    // Sync fields
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    
    @ColumnInfo(name = "remote_id")
    val remoteId: String? = null,
    
    @ColumnInfo(name = "last_modified")
    val lastModified: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class SyncStatus {
    SYNCED,
    PENDING_CREATE,
    PENDING_UPDATE,
    PENDING_DELETE,
    CONFLICT
}
