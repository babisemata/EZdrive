package com.example.ezdrive.model

data class Payment(
    val paymentId: Int,
    val bookingId: Int,
    val amount: Double,
    val paymentDate: String, // Format "YYYY-MM-DD"
    val status: String // "Paid"
)