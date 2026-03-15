package com.praveen.expensetracker.ui.screens

import androidx.lifecycle.ViewModel
import com.praveen.expensetracker.data.repository.AuthRepository
import com.praveen.expensetracker.data.repository.CategoryRepository
import com.praveen.expensetracker.data.repository.ExpenseRepository
import com.praveen.expensetracker.data.repository.Result
import com.praveen.expensetracker.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    val expenseRepository: ExpenseRepository,
    val categoryRepository: CategoryRepository
) : ViewModel()
