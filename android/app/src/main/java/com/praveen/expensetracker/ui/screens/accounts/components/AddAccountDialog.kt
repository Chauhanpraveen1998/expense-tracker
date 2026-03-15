package com.praveen.expensetracker.ui.screens.accounts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType
import com.praveen.expensetracker.ui.screens.accounts.AddAccountState
import com.praveen.expensetracker.ui.theme.CustomShapes
import com.praveen.expensetracker.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    editingAccount: Account?,
    onDismiss: () -> Unit,
    onSave: (AddAccountState) -> Unit
) {
    var name by remember { mutableStateOf(editingAccount?.name ?: "") }
    var type by remember { mutableStateOf(editingAccount?.type ?: AccountType.BANK) }
    var balance by remember { mutableStateOf(editingAccount?.balance?.toString() ?: "") }
    var bankName by remember { mutableStateOf(editingAccount?.bankName ?: "") }
    var lastFourDigits by remember { mutableStateOf(editingAccount?.lastFourDigits ?: "") }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingAccount != null) "Edit Account" else "Add Account"
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Account Name") },
                    placeholder = { Text("e.g., HDFC Savings") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CustomShapes.TextField,
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = type.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Account Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = CustomShapes.TextField
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        AccountType.entries.forEach { accountType ->
                            DropdownMenuItem(
                                text = { Text(accountType.displayName) },
                                onClick = {
                                    type = accountType
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Current Balance") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CustomShapes.TextField,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text("₹ ") }
                )

                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name (Optional)") },
                    placeholder = { Text("e.g., HDFC Bank") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CustomShapes.TextField,
                    singleLine = true
                )

                if (type == AccountType.BANK || type == AccountType.CREDIT_CARD) {
                    OutlinedTextField(
                        value = lastFourDigits,
                        onValueChange = { if (it.length <= 4) lastFourDigits = it.filter { c -> c.isDigit() } },
                        label = { Text("Last 4 Digits (Optional)") },
                        placeholder = { Text("1234") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CustomShapes.TextField,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        AddAccountState(
                            name = name,
                            type = type,
                            balance = balance,
                            bankName = bankName,
                            lastFourDigits = lastFourDigits
                        )
                    )
                },
                enabled = name.isNotBlank() && balance.toDoubleOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
