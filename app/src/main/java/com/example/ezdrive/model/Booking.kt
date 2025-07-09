package com.example.ezdrive.model

data class Booking(
    val bookingId: Int,
    val userId: Int,
    val carId: Int,
    val carName: String,
    val carImage: ByteArray,
    val startDate: String, // Simpan sebagai teks, format "YYYY-MM-DD"
    val endDate: String,
    val totalPrice: Double,
    val status: String // Contoh: "Confirmed", "Completed"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Booking
        if (bookingId != other.bookingId) return false
        return true
    }

    override fun hashCode(): Int {
        return bookingId
    }
}
