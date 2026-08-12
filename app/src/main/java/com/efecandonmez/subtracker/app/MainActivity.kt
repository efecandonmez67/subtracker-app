package com.efecandonmez.subtracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.efecandonmez.subtracker.app.data.local.TokenStore
import com.efecandonmez.subtracker.app.data.network.RetrofitProvider
import com.efecandonmez.subtracker.app.ui.navigation.AppNavigation
import com.efecandonmez.subtracker.app.ui.theme.SubtrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenStore = TokenStore(applicationContext)
        val api = RetrofitProvider.create(tokenStore)

        setContent {
            SubtrackerAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        api = api,
                        tokenStore = tokenStore,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}