package com.efecandonmez.subtracker.app.data.network

import com.efecandonmez.subtracker.app.data.model.*
import retrofit2.http.*

interface SubtrackerApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @PUT("auth/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest)

    @GET("subscriptions")
    suspend fun getSubscriptions(): List<SubscriptionResponse>

    @POST("subscriptions")
    suspend fun createSubscription(@Body request: SubscriptionRequest): SubscriptionResponse

    @PUT("subscriptions/{id}")
    suspend fun updateSubscription(@Path("id") id: String, @Body request: SubscriptionRequest): SubscriptionResponse

    @DELETE("subscriptions/{id}")
    suspend fun deleteSubscription(@Path("id") id: String)

    @GET("known-services")
    suspend fun getKnownServices(): List<KnownService>

    @GET("subscriptions/summary")
    suspend fun getSummary(): SubscriptionSummary
}