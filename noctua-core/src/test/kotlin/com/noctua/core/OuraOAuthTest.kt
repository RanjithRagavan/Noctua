package com.noctua.core

import com.noctua.core.auth.OuraOAuth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OuraOAuthTest {

    @Test
    fun `authorization url carries required parameters`() {
        val url = OuraOAuth.authorizationUrl(
            clientId = "CLIENT123",
            redirectUri = "noctua://callback",
            scopes = listOf("daily", "heartrate"),
            state = "xyz",
        )
        assertTrue(url.startsWith("https://cloud.ouraring.com/oauth/authorize?"))
        assertTrue(url.contains("response_type=token"))
        assertTrue(url.contains("client_id=CLIENT123"))
        assertTrue(url.contains("redirect_uri=noctua%3A%2F%2Fcallback"))
        assertTrue(url.contains("scope=daily+heartrate"))
        assertTrue(url.contains("state=xyz"))
    }

    @Test
    fun `code flow uses response_type code`() {
        val url = OuraOAuth.authorizationUrl(
            clientId = "CLIENT123",
            redirectUri = "https://example.com/cb",
            useClientSideFlow = false,
        )
        assertTrue(url.contains("response_type=code"))
    }

    @Test
    fun `parse client-side redirect fragment`() {
        val parsed = OuraOAuth.parseClientSideRedirect(
            "noctua://callback#token_type=bearer&access_token=ABC123&expires_in=2592000&scope=daily&state=xyz",
        )
        assertEquals("ABC123", parsed.accessToken)
        assertEquals(2592000L, parsed.expiresIn)
        assertEquals("xyz", parsed.state)
        assertNull(parsed.error)
    }

    @Test
    fun `parse denial redirect`() {
        val parsed = OuraOAuth.parseClientSideRedirect(
            "noctua://callback?error=access_denied&state=xyz",
        )
        assertEquals("access_denied", parsed.error)
        assertNull(parsed.accessToken)
    }

    @Test
    fun `pkce verifier and challenge are distinct and url-safe`() {
        val verifier = OuraOAuth.generateCodeVerifier()
        val challenge = OuraOAuth.codeChallenge(verifier)
        assertTrue(verifier.length >= 43)
        assertNotNull(challenge)
        assertTrue(challenge.none { it == '+' || it == '/' || it == '=' })
    }
}
