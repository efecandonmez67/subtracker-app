package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SubscriptionListScreen(viewModel: SubscriptionListViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadSubscriptions() }

    when (val state = uiState) {
        is SubscriptionListUiState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is SubscriptionListUiState.Error -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(state.message)
        }

        is SubscriptionListUiState.Success -> {
            if (state.subscriptions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz abonelik eklemedin.")
                }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                    items(state.subscriptions) { sub ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text(sub.name, style = MaterialTheme.typography.titleMedium)
                                Text("${sub.price} ${sub.currency} · ${sub.billingCycle}")
                                Text("Sonraki ödeme: ${sub.nextPaymentDate}")
                            }
                        }
                    }
                }
            }
        }
    }
}