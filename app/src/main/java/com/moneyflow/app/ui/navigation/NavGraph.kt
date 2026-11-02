package com.moneyflow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moneyflow.app.MoneyFlowApp
import com.moneyflow.app.ui.screens.AboutScreen
import com.moneyflow.app.ui.screens.AddEditProjectScreen
import com.moneyflow.app.ui.screens.AddEditTransactionScreen
import com.moneyflow.app.ui.screens.DashboardScreen
import com.moneyflow.app.ui.screens.ProjectDetailScreen
import com.moneyflow.app.ui.screens.ProjectListScreen
import com.moneyflow.app.ui.screens.SplashScreen
import com.moneyflow.app.ui.viewmodel.DashboardViewModel
import com.moneyflow.app.ui.viewmodel.DashboardViewModelFactory
import com.moneyflow.app.ui.viewmodel.ProjectViewModel
import com.moneyflow.app.ui.viewmodel.ProjectViewModelFactory
import com.moneyflow.app.ui.viewmodel.TransactionViewModel
import com.moneyflow.app.ui.viewmodel.TransactionViewModelFactory

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Dashboard : Screen("dashboard")
    data object ProjectList : Screen("projects")
    data object AddEditProject : Screen("add_edit_project?projectId={projectId}") {
        fun createRoute(projectId: Long? = null) =
            if (projectId != null) "add_edit_project?projectId=$projectId" else "add_edit_project"
    }
    data object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Long) = "project_detail/$projectId"
    }
    data object AddEditTransaction : Screen("add_edit_transaction/{projectId}?transactionId={transactionId}") {
        fun createRoute(projectId: Long, transactionId: Long? = null) =
            if (transactionId != null) "add_edit_transaction/$projectId?transactionId=$transactionId"
            else "add_edit_transaction/$projectId"
    }
    data object About : Screen("about")
}

@Composable
fun MoneyFlowNavGraph() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MoneyFlowApp
    val repository = app.repository

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(repository)
    )
    val projectViewModel: ProjectViewModel = viewModel(
        factory = ProjectViewModelFactory(repository)
    )

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigate = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToProjects = { navController.navigate(Screen.ProjectList.route) },
                onNavigateToProjectDetail = { navController.navigate(Screen.ProjectDetail.createRoute(it)) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.ProjectList.route) {
            ProjectListScreen(
                viewModel = projectViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddProject = { navController.navigate(Screen.AddEditProject.createRoute()) },
                onNavigateToEditProject = { navController.navigate(Screen.AddEditProject.createRoute(it)) },
                onNavigateToProjectDetail = { navController.navigate(Screen.ProjectDetail.createRoute(it)) }
            )
        }

        composable(
            route = Screen.AddEditProject.route,
            arguments = listOf(navArgument("projectId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStack ->
            val projectId = backStack.arguments?.getLong("projectId")?.takeIf { it != -1L }
            AddEditProjectScreen(
                projectId = projectId,
                viewModel = projectViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStack ->
            val projectId = backStack.arguments?.getLong("projectId") ?: return@composable
            val transactionViewModel: TransactionViewModel = viewModel(
                key = "tx_vm_$projectId",
                factory = TransactionViewModelFactory(repository, projectId)
            )
            ProjectDetailScreen(
                projectId = projectId,
                projectViewModel = projectViewModel,
                transactionViewModel = transactionViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddEditTransaction.createRoute(projectId))
                },
                onNavigateToEditTransaction = { txId ->
                    navController.navigate(Screen.AddEditTransaction.createRoute(projectId, txId))
                }
            )
        }

        composable(
            route = Screen.AddEditTransaction.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("transactionId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val projectId = backStack.arguments?.getLong("projectId") ?: return@composable
            val transactionId = backStack.arguments?.getLong("transactionId")?.takeIf { it != -1L }
            val transactionViewModel: TransactionViewModel = viewModel(
                key = "tx_edit_vm_${projectId}_${transactionId ?: "new"}",
                factory = TransactionViewModelFactory(repository, projectId)
            )
            AddEditTransactionScreen(
                projectId = projectId,
                transactionId = transactionId,
                viewModel = transactionViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
