package dev.metrodemo.core.data

data class Product(
    val id: String,
    val title: String,
    val minAmount: Int,
    val maxAmount: Int,
    val minTermDays: Int,
    val maxTermDays: Int,
)
