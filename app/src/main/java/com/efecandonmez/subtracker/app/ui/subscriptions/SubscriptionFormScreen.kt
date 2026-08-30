package com.efecandonmez.subtracker.app.ui.subscriptions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.efecandonmez.subtracker.app.data.model.SubscriptionRequest
import com.efecandonmez.subtracker.app.data.model.SubscriptionResponse
import com.efecandonmez.subtracker.app.ui.theme.GradientEndLight
import com.efecandonmez.subtracker.app.ui.theme.GradientStartLight
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Color

private val CATEGORIES = listOf("Streaming", "Müzik", "Yazılım", "Oyun", "Fitness", "Eğitim", "Bulut Depolama", "Haber", "Diğer")

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFormScreen(
    viewModel: SubscriptionFormViewModel,
    existing: SubscriptionResponse? = null,
    knownServiceViewModel: KnownServiceViewModel,
    onSaved: () -> Unit
) {

    var selectedDomain by remember { mutableStateOf(existing?.let { null } as String?) }
    var expanded by remember { mutableStateOf(false) }
    val services by knownServiceViewModel.services.collectAsState()

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var currency by remember { mutableStateOf(existing?.currency ?: "TRY") }
    var billingCycle by remember { mutableStateOf(existing?.billingCycle ?: "MONTHLY") }
    var nextPaymentDate by remember { mutableStateOf(existing?.nextPaymentDate ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = existing?.nextPaymentDate?.let {
            java.time.LocalDate.parse(it).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SubscriptionFormUiState.Success) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Brush.horizontalGradient(listOf(GradientStartLight, GradientEndLight))),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                if (existing == null) "Yeni Abonelik" else "Aboneliği Düzenle",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(24.dp)
            )
        }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; selectedDomain = null; nameError = null },
                label = { Text("İsim (listeden seç veya yaz)") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                services.forEach { service ->
                    DropdownMenuItem(
                        text = { Text(service.name) },
                        onClick = {
                            name = service.name
                            selectedDomain = service.domain
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = price, onValueChange = { price = it; priceError = null },
            label = { Text("Fiyat") },
            isError = priceError != null,
            supportingText = { priceError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(value = currency, onValueChange = { currency = it }, label = { Text("Para Birimi (USD/EUR/TRY)") }, modifier = Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = billingCycle == "MONTHLY", onClick = { billingCycle = "MONTHLY" }, label = { Text("Aylık") })
            FilterChip(selected = billingCycle == "YEARLY", onClick = { billingCycle = "YEARLY" }, label = { Text("Yıllık") })
        }

        OutlinedTextField(
            value = nextPaymentDate,
            onValueChange = { },
            label = { Text("Sonraki Ödeme") },
            readOnly = true,
            isError = dateError != null,
            supportingText = { dateError?.let { Text(it) } },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Tarih seç")
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
        )
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            nextPaymentDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            dateError = null
                        }
                        showDatePicker = false
                    }) {
                        Text("Tamam")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("İptal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { },
                readOnly = true,
                label = { Text("Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                CATEGORIES.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        if (uiState is SubscriptionFormUiState.Error) {
            Text((uiState as SubscriptionFormUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                var hasError = false

                if (name.isBlank()) { nameError = "İsim boş olamaz"; hasError = true }

                val priceValue = price.toDoubleOrNull()
                if (priceValue == null || priceValue <= 0) {
                    priceError = "Geçerli bir fiyat girin"; hasError = true
                }

                val dateRegex = Regex("""^\d{4}-\d{2}-\d{2}$""")
                if (!dateRegex.matches(nextPaymentDate)) {
                    dateError = "Tarih yyyy-MM-dd formatında olmalı"; hasError = true
                }

                if (!hasError) {
                    val request = SubscriptionRequest(
                        name = name,
                        price = priceValue!!,
                        currency = currency,
                        billingCycle = billingCycle,
                        nextPaymentDate = nextPaymentDate,
                        category = category.ifBlank { null },
                        serviceDomain = selectedDomain
                    )
                    if (existing == null) viewModel.createSubscription(request)
                    else viewModel.updateSubscription(existing.id, request)
                }
            },
            enabled = uiState !is SubscriptionFormUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState is SubscriptionFormUiState.Loading) "Kaydediliyor..." else "Kaydet")
        }
    }
} }