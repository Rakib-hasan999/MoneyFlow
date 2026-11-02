package com.moneyflow.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneyflow.app.R
import com.moneyflow.app.data.Transaction
import com.moneyflow.app.data.TransactionType
import com.moneyflow.app.ui.theme.Blue50
import com.moneyflow.app.ui.theme.Blue700
import com.moneyflow.app.ui.theme.CardBackground
import com.moneyflow.app.ui.theme.Green100
import com.moneyflow.app.ui.theme.Green700
import com.moneyflow.app.ui.theme.Red100
import com.moneyflow.app.ui.theme.Red700
import com.moneyflow.app.ui.theme.TextSecondary
import com.moneyflow.app.ui.viewmodel.ProjectViewModel
import com.moneyflow.app.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    projectViewModel: ProjectViewModel,
    transactionViewModel: TransactionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Long) -> Unit
) {
    val projects by projectViewModel.projects.collectAsStateWithLifecycle()
    val project = projects.find { it.id == projectId }
    val transactions by transactionViewModel.transactions.collectAsStateWithLifecycle()
    val totalIncome by transactionViewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by transactionViewModel.totalExpense.collectAsStateWithLifecycle()
    val netProfit by transactionViewModel.netProfit.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf<Transaction?>(null) }

    val fmt = remember {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply { maximumFractionDigits = 2 }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = project?.name ?: stringResource(R.string.title_project_fallback),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("project_detail_back_button")
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = Blue700,
                contentColor = CardBackground,
                modifier = Modifier.testTag("add_transaction_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_transaction)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .testTag("project_detail_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ProjectFinanceSummaryCard(totalIncome, totalExpense, netProfit, fmt)
                }

                item {
                    Text(
                        text = stringResource(R.string.transactions_header),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (transactions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("empty_transactions_card"),
                            colors = CardDefaults.cardColors(containerColor = Blue50)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Blue700.copy(alpha = 0.5f)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.empty_transactions_title),
                                    color = TextSecondary
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = onNavigateToAddTransaction,
                                    colors = ButtonDefaults.buttonColors(containerColor = Blue700),
                                    modifier = Modifier.testTag("empty_add_transaction_button")
                                ) {
                                    Text(stringResource(R.string.btn_add_transaction))
                                }
                            }
                        }
                    }
                } else {
                    items(transactions, key = { it.id }) { tx ->
                        TransactionItem(
                            transaction = tx,
                            fmt = fmt,
                            onEdit = { onNavigateToEditTransaction(tx.id) },
                            onDelete = { showDeleteDialog = tx }
                        )
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    showDeleteDialog?.let { tx ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.dialog_delete_transaction_title)) },
            text = { Text(stringResource(R.string.dialog_delete_transaction_message, tx.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionViewModel.deleteTransaction(tx)
                        showDeleteDialog = null
                    },
                    modifier = Modifier.testTag("confirm_delete_transaction_button")
                ) {
                    Text(stringResource(R.string.btn_delete), color = Red700)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null },
                    modifier = Modifier.testTag("cancel_delete_transaction_button")
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun ProjectFinanceSummaryCard(
    income: Double,
    expense: Double,
    net: Double,
    fmt: NumberFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_finance_summary_card"),
        colors = CardDefaults.cardColors(containerColor = Blue700),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.project_summary_title),
                color = CardBackground.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "৳ ${fmt.format(net)}",
                color = if (net >= 0) Green100 else Red100,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (net >= 0) {
                    stringResource(R.string.dashboard_net_profit)
                } else {
                    stringResource(R.string.dashboard_net_loss)
                },
                color = CardBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = stringResource(R.string.total_income_label),
                        color = CardBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "৳${fmt.format(income)}",
                        color = Green100,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.total_expense_label),
                        color = CardBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "৳${fmt.format(expense)}",
                        color = Red100,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    fmt: NumberFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val dateStr = remember(transaction.createdAt) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.createdAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isIncome) Green100 else Red100,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isIncome) {
                            Icons.AutoMirrored.Filled.TrendingUp
                        } else {
                            Icons.AutoMirrored.Filled.TrendingDown
                        },
                        contentDescription = null,
                        tint = if (isIncome) Green700 else Red700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${if (isIncome) "+" else "-"}৳${fmt.format(transaction.amount)}",
                color = if (isIncome) Green700 else Red700,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(4.dp))
            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.testTag("edit_transaction_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_edit),
                        tint = Blue700,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_transaction_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_delete),
                        tint = Red700,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
