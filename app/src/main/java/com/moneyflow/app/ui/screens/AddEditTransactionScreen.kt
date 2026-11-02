package com.moneyflow.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moneyflow.app.R
import com.moneyflow.app.data.Transaction
import com.moneyflow.app.data.TransactionType
import com.moneyflow.app.ui.theme.Blue700
import com.moneyflow.app.ui.theme.CardBackground
import com.moneyflow.app.ui.theme.Green700
import com.moneyflow.app.ui.theme.Red700
import com.moneyflow.app.ui.theme.SurfaceLight
import com.moneyflow.app.ui.theme.TextSecondary
import com.moneyflow.app.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    projectId: Long,
    transactionId: Long?,
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val isEditing = transactionId != null
    var existingTx by remember { mutableStateOf<Transaction?>(null) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }
    var note by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.getTransactionById(transactionId)?.let { tx ->
                existingTx = tx
                title = tx.title
                amount = if (tx.amount % 1.0 == 0.0) tx.amount.toLong().toString() else tx.amount.toString()
                selectedType = tx.type
                note = tx.note
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) {
                            stringResource(R.string.title_edit_transaction)
                        } else {
                            stringResource(R.string.title_add_transaction)
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_edit_tx_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = CardBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue700,
                    titleContentColor = CardBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.label_transaction_type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TypeButton(
                        label = stringResource(R.string.dashboard_income),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        selected = selectedType == TransactionType.INCOME,
                        selectedColor = Green700,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("type_income_button"),
                        onClick = { selectedType = TransactionType.INCOME }
                    )
                    TypeButton(
                        label = stringResource(R.string.dashboard_expense),
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        selected = selectedType == TransactionType.EXPENSE,
                        selectedColor = Red700,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("type_expense_button"),
                        onClick = { selectedType = TransactionType.EXPENSE }
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text(stringResource(R.string.label_title)) },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text(stringResource(R.string.error_title_required)) }
                    } else {
                        null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_title_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, tint = Blue700)
                    }
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = false
                    },
                    label = { Text(stringResource(R.string.label_amount)) },
                    isError = amountError,
                    supportingText = if (amountError) {
                        { Text(stringResource(R.string.error_amount_invalid)) }
                    } else {
                        null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_amount_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = Blue700)
                    }
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.label_note)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("transaction_note_input"),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = Blue700)
                    }
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        val amountVal = amount.toDoubleOrNull()
                        titleError = title.isBlank()
                        amountError = amountVal == null || amountVal <= 0
                        if (titleError || amountError) return@Button

                        scope.launch {
                            if (isEditing && transactionId != null) {
                                val txToUpdate = existingTx ?: viewModel.getTransactionById(transactionId)
                                if (txToUpdate != null) {
                                    viewModel.updateTransaction(
                                        txToUpdate,
                                        title.trim(),
                                        amountVal!!,
                                        selectedType,
                                        note.trim()
                                    )
                                }
                            } else {
                                viewModel.addTransaction(
                                    title.trim(),
                                    amountVal!!,
                                    selectedType,
                                    note.trim()
                                )
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_transaction_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == TransactionType.INCOME) Green700 else Red700
                    )
                ) {
                    Icon(if (isEditing) Icons.Default.Save else Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) {
                            stringResource(R.string.btn_save_changes)
                        } else {
                            stringResource(R.string.btn_add_transaction)
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (selected) selectedColor else SurfaceLight
    val contentColor = if (selected) CardBackground else TextSecondary

    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selected) 4.dp else 0.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
