package com.example.ezdrive.model

data class Car(
    val carid: Int = 0, // Beri nilai default
    val merk: String?, // Jadikan nullable agar lebih aman
    val model: String?,
    val tahun: Int?,
    val kapasitas: Int?,
    val hargaPerHari: Double?,
    val foto: ByteArray?,
    val category: String?,
    val transmission: String?,
    val isAvailable: Boolean = true,
    val id_user: Int?
) {
    // Override ini diperlukan jika Anda membandingkan objek di dalam list
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Car
        if (carid != other.carid) return false
        return true
    }
    override fun hashCode(): Int {
        return carid
    }
}