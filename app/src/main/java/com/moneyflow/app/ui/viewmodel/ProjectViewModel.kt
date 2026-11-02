package com.moneyflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneyflow.app.data.Project
import com.moneyflow.app.repository.MoneyFlowRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectViewModel(private val repository: MoneyFlowRepository) : ViewModel() {

    val projects: StateFlow<List<Project>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getProjectById(id: Long): Project? = repository.getProjectById(id)

    fun addProject(name: String, description: String) {
        viewModelScope.launch {
            repository.insertProject(Project(name = name, description = description))
        }
    }

    fun updateProject(project: Project, name: String, description: String) {
        viewModelScope.launch {
            repository.updateProject(project.copy(name = name, description = description))
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch { repository.deleteProject(project) }
    }
}

class ProjectViewModelFactory(private val repository: MoneyFlowRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProjectViewModel(repository) as T
    }
}
