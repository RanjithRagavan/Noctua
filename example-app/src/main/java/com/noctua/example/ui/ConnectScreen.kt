package com.noctua.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.noctua.core.auth.OuraOAuth

/**
 * Two ways in:
 *  1. Paste a Personal Access Token (quickest for personal use).
 *  2. OAuth2 client-side flow — opens Oura's consent page; the issued token
 *     lands back in this app via the noctua://callback deep link.
 *
 * Register an OAuth application at https://cloud.ouraring.com/oauth/applications
 * with redirect URI `noctua://callback`, then paste your client ID below.
 */
@Composable
fun ConnectScreen(
    state: NoctuaUiState,
    onToken: (String) -> Unit,
    onDemo: () -> Unit,
) {
    var token by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Connect Oura", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        // --- Option 1: token ---
        Text("Option 1 — Personal Access Token", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Oura token") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Button(
            onClick = { if (token.isNotBlank()) onToken(token.trim()) },
            enabled = token.isNotBlank() && !state.loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.loading && !state.demoMode) "Connecting…" else "Connect with token")
        }

        Spacer(Modifier.height(8.dp))

        // --- Option 2: OAuth ---
        Text("Option 2 — OAuth2 sign-in", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = clientId,
            onValueChange = { clientId = it },
            label = { Text("Oura application client ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Button(
            onClick = {
                val url = OuraOAuth.authorizationUrl(
                    clientId = clientId.trim(),
                    redirectUri = "noctua://callback",
                    state = OuraOAuth.generateCodeVerifier(),
                )
                uriHandler.openUri(url)
            },
            enabled = clientId.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign in with Oura")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onDemo, modifier = Modifier.fillMaxWidth()) {
            Text("Back to demo mode")
        }
    }
}
