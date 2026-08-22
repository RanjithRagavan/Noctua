package com.noctua.core.api

import com.noctua.core.model.OuraToken
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * OAuth2 token endpoint — `POST https://api.ouraring.com/oauth/token`.
 * Used by the authorization-code exchange and the refresh-token grant.
 */
interface TokenApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun exchangeCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String?,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String?,
        @Field("code_verifier") codeVerifier: String? = null,
    ): OuraToken

    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String?,
    ): OuraToken
}
