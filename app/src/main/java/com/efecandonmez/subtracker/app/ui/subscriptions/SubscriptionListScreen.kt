package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.efecandonmez.subtracker.app.ui.theme.GradientEndLight
import com.efecandonmez.subtracker.app.ui.theme.GradientStartLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionListScreen(
    viewModel: SubscriptionListViewModel,
    summaryViewModel: SummaryViewModel,
    onAddClick: () -> Unit,
    onDeleteConfirmed: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val summary by summaryViewModel.summary.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSubscriptions()
        summaryViewModel.loadSummary()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is SubscriptionListUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is SubscriptionListUiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(state.message)
            }

            is SubscriptionListUiState.Success -> {
                if (state.subscriptions.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Henüz abonelik eklemedin",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Sağ alttaki + butonuyla ilk aboneliğini ekle",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                        item {
                            summary?.let { s ->
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(GradientStartLight, GradientEndLight)
                                            )
                                        )
                                ) {
                                    Column(Modifier.padding(20.dp)) {
                                        Text("Bu ay toplam", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                                        Text(
                                            "%.0f".format(s.totalMonthly),
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = Color.White
                                        )

                                        if (s.byCategory.isNotEmpty()) {
                                            Spacer(Modifier.height(16.dp))
                                            DonutChart(data = s.byCategory, textColor = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                        items(state.subscriptions, key = { it.id }) { sub ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        onDeleteConfirmed(sub.id)
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                modifier = Modifier.padding(vertical = 6.dp),
                                backgroundContent = {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(MaterialTheme.colorScheme.error),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Sil",
                                            tint = Color.White,
                                            modifier = Modifier.padding(end = 20.dp)
                                        )
                                    }
                                }
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Row(
                                        Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (sub.serviceDomain != null) {
                                                AsyncImage(
                                                    model = "https://www.google.com/s2/favicons?domain=${sub.serviceDomain}&sz=128",
                                                    contentDescription = sub.name,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                                                    error = painterResource(android.R.drawable.ic_menu_gallery)
                                                )
                                            } else {
                                                Box(
                                                    Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        sub.name.take(1).uppercase(),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.width(12.dp))

                                            Column {
                                                Text(sub.name, style = MaterialTheme.typography.titleMedium)
                                                Text(
                                                    "Sonraki ödeme: ${sub.nextPaymentDate}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                "${sub.price} ${sub.currency}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                if (sub.billingCycle == "MONTHLY") "Aylık" else "Yıllık",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}