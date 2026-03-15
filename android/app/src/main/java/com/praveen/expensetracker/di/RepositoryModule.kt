package com.praveen.expensetracker.di

import com.praveen.expensetracker.data.repository.AccountRepositoryImpl
import com.praveen.expensetracker.data.repository.TransactionRepositoryImpl
import com.praveen.expensetracker.domain.repository.AccountRepository
import com.praveen.expensetracker.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository
}
