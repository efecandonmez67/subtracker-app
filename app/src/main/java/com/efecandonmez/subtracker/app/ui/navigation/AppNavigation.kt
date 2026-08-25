package com.efecandonmez.subtracker.app.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.efecandonmez.subtracker.app.data.local.TokenStore
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import com.efecandonmez.subtracker.app.ui.auth.AuthViewModel
import com.efecandonmez.subtracker.app.ui.auth.AuthViewModelFactory
import com.efecandonmez.subtracker.app.ui.auth.LoginScreen
import com.efecandonmez.subtracker.app.ui.auth.RegisterScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.KnownServiceViewModel
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionFormScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionFormViewModel
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionListScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionListViewModel
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

    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
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
            SubscriptionListScreen(
                viewModel = listViewModel,
                onAddClick = { navController.navigate("subscription_form") },
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
    }
}