package com.moneyflow.app.repository

import com.moneyflow.app.data.Project
import com.moneyflow.app.data.ProjectDao
import com.moneyflow.app.data.Transaction
import com.moneyflow.app.data.TransactionDao
import kotlinx.coroutines.flow.Flow

class MoneyFlowRepository(
    private val projectDao: ProjectDao,
    private val transactionDao: TransactionDao
) {
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()
    suspend fun getProjectById(id: Long): Project? = projectDao.getProjectById(id)
    suspend fun insertProject(project: Project): Long = projectDao.insertProject(project)
    suspend fun updateProject(project: Project) = projectDao.updateProject(project)
    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)

    fun getTransactionsForProject(projectId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsForProject(projectId)

    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTotalIncomeForProject(projectId: Long): Flow<Double> =
        transactionDao.getTotalIncomeForProject(projectId)

    fun getTotalExpenseForProject(projectId: Long): Flow<Double> =
        transactionDao.getTotalExpenseForProject(projectId)

    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)
}
