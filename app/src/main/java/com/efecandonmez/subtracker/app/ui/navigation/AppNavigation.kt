package com.efecandonmez.subtracker.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.efecandonmez.subtracker.app.data.local.TokenStore
import com.efecandonmez.subtracker.app.data.network.SubtrackerApi
import com.efecandonmez.subtracker.app.ui.auth.AuthViewModelFactory
import com.efecandonmez.subtracker.app.ui.auth.LoginScreen
import com.efecandonmez.subtracker.app.ui.auth.RegisterScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionFormScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionFormViewModel
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionListScreen
import com.efecandonmez.subtracker.app.ui.subscriptions.SubscriptionListViewModel

@Composable
fun AppNavigation(api: SubtrackerApi, tokenStore: TokenStore, modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val authViewModel: com.efecandonmez.subtracker.app.ui.auth.AuthViewModel =
                viewModel(factory = AuthViewModelFactory(api, tokenStore))

            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate("subscriptions") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            val authViewModel: com.efecandonmez.subtracker.app.ui.auth.AuthViewModel =
                viewModel(factory = AuthViewModelFactory(api, tokenStore))

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
                onAddClick = { navController.navigate("subscription_form") }
            )
        }
        composable("subscription_form") {
            val formViewModel: SubscriptionFormViewModel = viewModel { SubscriptionFormViewModel(api) }
            SubscriptionFormScreen(
                viewModel = formViewModel,
                onSaved = { navController.popBackStack() }
            )
        }
    }
}