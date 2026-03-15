package com.praveen.expensetracker.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.praveen.expensetracker.ui.components.PrimaryButton
import com.praveen.expensetracker.ui.screens.add.components.AmountInputSection
import com.praveen.expensetracker.ui.screens.add.components.CategorySelectorContent
import com.praveen.expensetracker.ui.screens.add.components.DatePickerModal
import com.praveen.expensetracker.ui.screens.add.components.DateTimeSelector
import com.praveen.expensetracker.ui.screens.add.components.TimePickerModal
import com.praveen.expensetracker.ui.screens.add.components.TransactionTypeToggle
import com.praveen.expensetracker.ui.theme.CustomShapes
import com.praveen.expensetracker.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    onTransactionAdded: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onTransactionAdded()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(AddTransactionEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AddTransactionEvent.SaveTransaction) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = Spacing.screenPadding)
        ) {
            AmountInputSection(
                amount = uiState.amount,
                transactionType = uiState.transactionType,
                onAmountChange = { viewModel.onEvent(AddTransactionEvent.AmountChanged(it)) },
                error = uiState.amountError
            )
            
            Spacer(modifier = Modifier.height(Spacing.large))
            
            TransactionTypeToggle(
                selectedType = uiState.transactionType,
                onTypeSelected = { viewModel.onEvent(AddTransactionEvent.TransactionTypeChanged(it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing.large))
            
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(Spacing.small))
            
            CategorySelectorContent(
                transactionType = uiState.transactionType,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.onEvent(AddTransactionEvent.CategorySelected(it)) }
            )
            
            if (uiState.categoryError != null) {
                Text(
                    text = uiState.categoryError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.large))
            
            OutlinedTextField(
                value = uiState.merchantName,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.MerchantNameChanged(it)) },
                label = { Text("Merchant / Description") },
                placeholder = { Text("e.g., Swiggy, Amazon, Salary") },
                modifier = Modifier.fillMaxWidth(),
                shape = CustomShapes.TextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true,
                isError = uiState.merchantError != null,
                supportingText = uiState.merchantError?.let { { Text(it) } }
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            DateTimeSelector(
                selectedDate = uiState.selectedDate,
                selectedTime = uiState.selectedTime,
                onDateClick = { viewModel.onEvent(AddTransactionEvent.ToggleDatePicker) },
                onTimeClick = { viewModel.onEvent(AddTransactionEvent.ToggleTimePicker) }
            )
            
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.NoteChanged(it)) },
                label = { Text("Note (Optional)") },
                placeholder = { Text("Add a note...") },
                modifier = Modifier.fillMaxWidth(),
                shape = CustomShapes.TextField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                minLines = 2,
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.height(Spacing.xxLarge))
            
            PrimaryButton(
                text = "Save Transaction",
                onClick = { viewModel.onEvent(AddTransactionEvent.SaveTransaction) },
                isLoading = uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(Spacing.huge))
        }
        
        DatePickerModal(
            isVisible = uiState.showDatePicker,
            selectedDate = uiState.selectedDate,
            onDateSelected = { viewModel.onEvent(AddTransactionEvent.DateSelected(it)) },
            onDismiss = { viewModel.onEvent(AddTransactionEvent.ToggleDatePicker) }
        )
        
        TimePickerModal(
            isVisible = uiState.showTimePicker,
            selectedTime = uiState.selectedTime,
            onTimeSelected = { viewModel.onEvent(AddTransactionEvent.TimeSelected(it)) },
            onDismiss = { viewModel.onEvent(AddTransactionEvent.ToggleTimePicker) }
        )
    }
}
