package com.praveen.expensetracker.data.local

import androidx.compose.ui.graphics.Color
import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.domain.repository.AccountRepository
import com.praveen.expensetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataSeeder @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {
    suspend fun seedIfEmpty() {
        val existingAccounts = accountRepository.getAllAccounts().first()
        if (existingAccounts.isEmpty()) {
            seedAccounts()
        }
        
        val existingTransactions = transactionRepository.getAllTransactions().first()
        if (existingTransactions.isEmpty()) {
            seedTransactions()
        }
    }
    
    private suspend fun seedAccounts() {
        val accounts = listOf(
            Account(
                id = UUID.randomUUID().toString(),
                name = "HDFC Savings",
                type = AccountType.BANK,
                balance = 125430.50,
                bankName = "HDFC Bank",
                lastFourDigits = "4521",
                colorPrimary = Color(0xFF1E3A8A),
                colorSecondary = Color(0xFF3B82F6)
            ),
            Account(
                id = UUID.randomUUID().toString(),
                name = "Amazon Pay ICICI",
                type = AccountType.CREDIT_CARD,
                balance = 15680.00,
                bankName = "ICICI Bank",
                lastFourDigits = "9087",
                colorPrimary = Color(0xFF7C3AED),
                colorSecondary = Color(0xFFA78BFA)
            ),
            Account(
                id = UUID.randomUUID().toString(),
                name = "Paytm Wallet",
                type = AccountType.WALLET,
                balance = 2500.00,
                colorPrimary = Color(0xFF059669),
                colorSecondary = Color(0xFF34D399)
            )
        )
        
        accounts.forEach { accountRepository.insertAccount(it) }
    }
    
    private suspend fun seedTransactions() {
        val today = LocalDate.now()
        
        val transactions = listOf(
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 345.0,
                merchantName = "Swiggy",
                category = Category.FOOD_DINING,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today, LocalTime.of(14, 30)),
                note = "Lunch order"
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 180.0,
                merchantName = "Uber",
                category = Category.TRANSPORT,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today, LocalTime.of(10, 15))
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 1299.0,
                merchantName = "Amazon",
                category = Category.SHOPPING,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today, LocalTime.of(9, 0)),
                note = "Phone case"
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 75000.0,
                merchantName = "Salary Credit",
                category = Category.SALARY,
                type = TransactionType.INCOME,
                dateTime = LocalDateTime.of(today.minusDays(1), LocalTime.of(10, 0)),
                note = "March 2024 Salary"
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 2340.0,
                merchantName = "BigBasket",
                category = Category.GROCERIES,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today.minusDays(1), LocalTime.of(18, 45))
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 499.0,
                merchantName = "Netflix",
                category = Category.ENTERTAINMENT,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today.minusDays(1), LocalTime.of(0, 1)),
                isRecurring = true
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 850.0,
                merchantName = "Domino's",
                category = Category.FOOD_DINING,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today.minusDays(2), LocalTime.of(20, 30))
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 1500.0,
                merchantName = "Indian Oil",
                category = Category.FUEL,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today.minusDays(2), LocalTime.of(8, 15))
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 3200.0,
                merchantName = "Electricity Bill",
                category = Category.BILLS_UTILITIES,
                type = TransactionType.EXPENSE,
                dateTime = LocalDateTime.of(today.minusDays(3), LocalTime.of(11, 0))
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = 15000.0,
                merchantName = "Freelance Payment",
                category = Category.FREELANCE,
                type = TransactionType.INCOME,
                dateTime = LocalDateTime.of(today.minusDays(5), LocalTime.of(16, 30)),
                note = "UI Design Project"
            )
        )
        
        transactions.forEach { transactionRepository.insertTransaction(it) }
    }
}
