package com.moneyflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneyflow.app.data.Transaction
import com.moneyflow.app.data.TransactionType
import com.moneyflow.app.repository.MoneyFlowRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: MoneyFlowRepository,
    val projectId: Long
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.getTransactionsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netProfit: StateFlow<Double> = combine(totalIncome, totalExpense) { inc, exp -> inc - exp }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    suspend fun getTransactionById(id: Long): Transaction? =
        transactions.value.find { it.id == id } ?: repository.getTransactionById(id)

    fun addTransaction(title: String, amount: Double, type: TransactionType, note: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(projectId = projectId, title = title, amount = amount, type = type, note = note)
            )
        }
    }

    fun updateTransaction(transaction: Transaction, title: String, amount: Double, type: TransactionType, note: String) {
        viewModelScope.launch {
            repository.updateTransaction(transaction.copy(title = title, amount = amount, type = type, note = note))
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.deleteTransaction(transaction) }
    }
}

class TransactionViewModelFactory(
    private val repository: MoneyFlowRepository,
    private val projectId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TransactionViewModel(repository, projectId) as T
    }
}
