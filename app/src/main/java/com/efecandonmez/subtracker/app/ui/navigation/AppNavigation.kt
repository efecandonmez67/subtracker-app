package com.efecandonmez.subtracker.app.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.efecandonmez.subtracker.app.data.local.TokenStore
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import com.efecandonmez.subtracker.app.ui.auth.AuthViewModel
import com.efecandonmez.subtracker.app.ui.auth.AuthViewModelFactory
import com.efecandonmez.subtracker.app.ui.auth.LoginScreen
import com.efecandonmez.subtracker.app.ui.auth.RegisterScreen
import com.efecandonmez.subtracker.app.ui.badges.BadgeScreen
import com.efecandonmez.subtracker.app.ui.badges.BadgeViewModel
import com.efecandonmez.subtracker.app.ui.subscriptions.KnownServiceViewModel
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionFormScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionFormViewModel
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionListScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionListViewModel
import com.efecandonmez.subtracker.app.ui.subscriptions.SummaryViewModel
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(api: SubtrackerApi, tokenStore: TokenStore, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        tokenStore.tokenFlow.collectLatest { token ->
            if (token == null && navController.currentDestination?.route != "login" && navController.currentDestination?.route != "register") {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute == "subscriptions" || currentRoute == "badges"

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (currentRoute == "subscriptions") {
                FloatingActionButton(onClick = { navController.navigate("subscription_form") }) {
                    Icon(Icons.Default.Add, contentDescription = "Ekle")
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = currentRoute == "subscriptions",
                        onClick = { navController.navigate("subscriptions") { popUpTo("subscriptions") { inclusive = true } } },
                        icon = { Icon(Icons.Default.List, contentDescription = "Abonelikler") },
                        label = { Text("Abonelikler") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "badges",
                        onClick = { navController.navigate("badges") { popUpTo("subscriptions") } },
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Rozetler") },
                        label = { Text("Rozetler") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(api, tokenStore))
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { navController.navigate("subscriptions") { popUpTo("login") { inclusive = true } } },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(api, tokenStore))
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { navController.navigate("subscriptions") { popUpTo("login") { inclusive = true } } },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable("subscriptions") {
                val listViewModel: SubscriptionListViewModel = viewModel { SubscriptionListViewModel(api) }
                val summaryViewModel: SummaryViewModel = viewModel { SummaryViewModel(api) }
                SubscriptionListScreen(
                    viewModel = listViewModel,
                    summaryViewModel = summaryViewModel,
                    onDeleteConfirmed = { id -> listViewModel.deleteSubscription(id) }
                )
            }
            composable("subscription_form") {
                val formViewModel: SubscriptionFormViewModel = viewModel { SubscriptionFormViewModel(api) }
                val knownServiceViewModel: KnownServiceViewModel = viewModel { KnownServiceViewModel(api) }
                SubscriptionFormScreen(
                    viewModel = formViewModel,
                    knownServiceViewModel = knownServiceViewModel,
                    onSaved = { navController.popBackStack() }
                )
            }
            composable("badges") {
                val badgeViewModel: BadgeViewModel = viewModel { BadgeViewModel(api) }
                BadgeScreen(viewModel = badgeViewModel)
            }
        }
    }
}