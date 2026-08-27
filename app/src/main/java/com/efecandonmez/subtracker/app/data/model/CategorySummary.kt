package com.efecandonmez.subtracker.app.data.model

data class CategorySummary(val category: String, val monthlyTotal: Double)
data class SubscriptionSummary(
    val totalMonthly: Double,
    val totalYearly: Double,
    val byCategory: List<CategorySummary>
)