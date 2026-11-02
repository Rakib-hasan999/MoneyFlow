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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneyflow.app.R
import com.moneyflow.app.ui.theme.Blue50
import com.moneyflow.app.ui.theme.Blue700
import com.moneyflow.app.ui.theme.CardBackground
import com.moneyflow.app.ui.theme.Green100
import com.moneyflow.app.ui.theme.Green700
import com.moneyflow.app.ui.theme.Red100
import com.moneyflow.app.ui.theme.Red700
import com.moneyflow.app.ui.theme.TextSecondary
import com.moneyflow.app.ui.viewmodel.DashboardViewModel
import com.moneyflow.app.ui.viewmodel.ProjectSummary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToProjectDetail: (Long) -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val summaries by viewModel.projectSummaries.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val netProfit by viewModel.netProfit.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue700,
                    titleContentColor = CardBackground
                ),
                actions = {
                    IconButton(
                        onClick = onNavigateToAbout,
                        modifier = Modifier.testTag("about_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.cd_about),
                            tint = CardBackground
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToProjects,
                icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                text = { Text(stringResource(R.string.fab_projects)) },
                containerColor = Blue700,
                contentColor = CardBackground,
                modifier = Modifier.testTag("projects_fab")
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .testTag("dashboard_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OverallSummaryCard(totalIncome, totalExpense, netProfit)
                }

                item {
                    Text(
                        text = stringResource(R.string.dashboard_projects_overview),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (summaries.isEmpty()) {
                    item { EmptyProjectsState(onNavigateToProjects) }
                } else {
                    items(summaries, key = { it.project.id }) { summary ->
                        ProjectSummaryCard(
                            summary = summary,
                            onClick = { onNavigateToProjectDetail(summary.project.id) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun OverallSummaryCard(income: Double, expense: Double, net: Double) {
    val fmt = remember {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply { maximumFractionDigits = 2 }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("overall_summary_card"),
        colors = CardDefaults.cardColors(containerColor = Blue700),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.dashboard_overall_summary),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(stringResource(R.string.dashboard_income), income, Green100, fmt)
                SummaryItem(stringResource(R.string.dashboard_expense), expense, Red100, fmt)
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, amount: Double, color: Color, fmt: NumberFormat) {
    Column {
        Text(
            text = label,
            color = CardBackground.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "৳ ${fmt.format(amount)}",
            color = color,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProjectSummaryCard(summary: ProjectSummary, onClick: () -> Unit) {
    val fmt = remember {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply { maximumFractionDigits = 2 }
    }
    val net = summary.netProfit
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_summary_card_${summary.project.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.project.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (summary.project.description.isNotBlank()) {
                    Text(
                        text = summary.project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "↑ ৳${fmt.format(summary.totalIncome)}",
                        color = Green700,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "↓ ৳${fmt.format(summary.totalExpense)}",
                        color = Red700,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳${fmt.format(net)}",
                    color = if (net >= 0) Green700 else Red700,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (net >= 0) {
                        stringResource(R.string.profit_label)
                    } else {
                        stringResource(R.string.loss_label)
                    },
                    color = if (net >= 0) Green700 else Red700,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun EmptyProjectsState(onAdd: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("empty_projects_card"),
        colors = CardDefaults.cardColors(containerColor = Blue50)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Blue700
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.empty_projects_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.empty_projects_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = Blue700),
                modifier = Modifier.testTag("empty_add_project_button")
            ) {
                Text(stringResource(R.string.btn_add_project))
            }
        }
    }
}
