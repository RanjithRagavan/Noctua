package com.noctua.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noctua.core.auth.OuraOAuth
import com.noctua.example.ui.ConnectScreen
import com.noctua.example.ui.DashboardScreen
import com.noctua.example.ui.InsightsScreen
import com.noctua.example.ui.NoctuaViewModel
import com.noctua.example.ui.theme.NoctuaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NoctuaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoctuaTheme {
                NoctuaApp(viewModel)
            }
        }
        handleOAuthRedirect(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthRedirect(intent)
    }

    /** Oura redirects here after OAuth consent: noctua://callback#access_token=... */
    private fun handleOAuthRedirect(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme != "noctua" || uri.host != "callback") return
        val parsed = OuraOAuth.parseClientSideRedirect(uri.toString())
        parsed.accessToken?.let(viewModel::connectWithToken)
    }
}

private const val ROUTE_DASHBOARD = "dashboard"
private const val ROUTE_INSIGHTS = "insights"
private const val ROUTE_CONNECT = "connect"

@Composable
fun NoctuaApp(viewModel: NoctuaViewModel) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_DASHBOARD

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == ROUTE_DASHBOARD,
                    onClick = { navController.navigate(ROUTE_DASHBOARD) { popUpTo(ROUTE_DASHBOARD) } },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                )
                NavigationBarItem(
                    selected = currentRoute == ROUTE_INSIGHTS,
                    onClick = { navController.navigate(ROUTE_INSIGHTS) { popUpTo(ROUTE_DASHBOARD) } },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Coach") },
                    label = { Text("AI Coach") },
                )
                NavigationBarItem(
                    selected = currentRoute == ROUTE_CONNECT,
                    onClick = { navController.navigate(ROUTE_CONNECT) { popUpTo(ROUTE_DASHBOARD) } },
                    icon = { Icon(Icons.Default.Link, contentDescription = "Connect") },
                    label = { Text("Connect") },
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(navController = navController, startDestination = ROUTE_DASHBOARD) {
                composable(ROUTE_DASHBOARD) { DashboardScreen(state) }
                composable(ROUTE_INSIGHTS) { InsightsScreen(state) }
                composable(ROUTE_CONNECT) {
                    ConnectScreen(
                        state = state,
                        onToken = viewModel::connectWithToken,
                        onDemo = viewModel::loadDemo,
                    )
                }
            }
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
