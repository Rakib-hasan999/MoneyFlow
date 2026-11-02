package com.moneyflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneyflow.app.data.Project
import com.moneyflow.app.data.TransactionType
import com.moneyflow.app.repository.MoneyFlowRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProjectSummary(
    val project: Project,
    val totalIncome: Double,
    val totalExpense: Double,
    val netProfit: Double
)

class DashboardViewModel(private val repository: MoneyFlowRepository) : ViewModel() {

    private val _projects = repository.getAllProjects().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _allTransactions = repository.getAllTransactions().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val projectSummaries: StateFlow<List<ProjectSummary>> = combine(_projects, _allTransactions) { projects, transactions ->
        projects.map { project ->
            val projectTx = transactions.filter { it.projectId == project.id }
            val income = projectTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = projectTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            ProjectSummary(project, income, expense, income - expense)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = _allTransactions.map { txList ->
        txList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = _allTransactions.map { txList ->
        txList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netProfit: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}

class DashboardViewModelFactory(private val repository: MoneyFlowRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DashboardViewModel(repository) as T
    }
}
