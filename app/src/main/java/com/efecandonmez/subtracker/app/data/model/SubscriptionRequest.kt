package com.efecandonmez.subtracker.app.data.model

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String)
data class AuthResponse(val token: String, val expiresAt: Long)
data class FcmTokenRequest(val fcmToken: String)

data class SubscriptionRequest(
    val name: String,
    val price: Double,
    val currency: String,
    val billingCycle: String, // "MONTHLY" | "YEARLY"
    val nextPaymentDate: String, // "yyyy-MM-dd"
    val category: String?,
    val serviceDomain: String? = null
)

data class SubscriptionResponse(
    val id: String,
    val name: String,
    val price: Double,
    val currency: String,
    val billingCycle: String,
    val nextPaymentDate: String,
    val category: String?,
    val active: Boolean,
    val serviceDomain: String? = null
)