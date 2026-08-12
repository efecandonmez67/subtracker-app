package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.efecandonmez.subtracker.app.data.model.SubscriptionRequest
import com.efecandonmez.subtracker.app.data.model.SubscriptionResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFormScreen(
    viewModel: SubscriptionFormViewModel,
    existing: SubscriptionResponse? = null,
    onSaved: () -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var currency by remember { mutableStateOf(existing?.currency ?: "TRY") }
    var billingCycle by remember { mutableStateOf(existing?.billingCycle ?: "MONTHLY") }
    var nextPaymentDate by remember { mutableStateOf(existing?.nextPaymentDate ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SubscriptionFormUiState.Success) onSaved()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            if (existing == null) "Yeni Abonelik" else "Aboneliği Düzenle",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("İsim") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Fiyat") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = currency, onValueChange = { currency = it }, label = { Text("Para Birimi (USD/EUR/TRY)") }, modifier = Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = billingCycle == "MONTHLY", onClick = { billingCycle = "MONTHLY" }, label = { Text("Aylık") })
            FilterChip(selected = billingCycle == "YEARLY", onClick = { billingCycle = "YEARLY" }, label = { Text("Yıllık") })
        }

        OutlinedTextField(
            value = nextPaymentDate, onValueChange = { nextPaymentDate = it },
            label = { Text("Sonraki Ödeme (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())

        if (uiState is SubscriptionFormUiState.Error) {
            Text((uiState as SubscriptionFormUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val request = SubscriptionRequest(
                    name = name,
                    price = price.toDoubleOrNull() ?: 0.0,
                    currency = currency,
                    billingCycle = billingCycle,
                    nextPaymentDate = nextPaymentDate,
                    category = category.ifBlank { null }
                )
                if (existing == null) viewModel.createSubscription(request)
                else viewModel.updateSubscription(existing.id, request)
            },
            enabled = uiState !is SubscriptionFormUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState is SubscriptionFormUiState.Loading) "Kaydediliyor..." else "Kaydet")
        }
    }
}