package com.praveen.expensetracker.data.local

import androidx.room.TypeConverter
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }
    
    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }
    
    @TypeConverter
    fun toCategory(categoryName: String): Category {
        return try {
            Category.valueOf(categoryName)
        } catch (e: IllegalArgumentException) {
            Category.OTHER_EXPENSE
        }
    }
    
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }
    
    @TypeConverter
    fun toTransactionType(typeName: String): TransactionType {
        return try {
            TransactionType.valueOf(typeName)
        } catch (e: IllegalArgumentException) {
            TransactionType.EXPENSE
        }
    }
    
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
    
    @TypeConverter
    fun toStringList(data: String?): List<String>? {
        return data?.split(",")?.filter { it.isNotBlank() }
    }
}
