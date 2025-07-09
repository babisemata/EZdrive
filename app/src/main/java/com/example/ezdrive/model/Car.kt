package com.example.ezdrive.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Tambahkan anotasi @Parcelize
@Parcelize
data class Car(
    val carid: Int = 0,
    val merk: String?,
    val model: String?,
    val tahun: Int?,
    val kapasitas: Int?,
    val hargaPerHari: Double?,
    val foto: ByteArray?,
    val category: String?,
    val transmission: String?,
    val isAvailable: Boolean = true,
    val id_user: Int?
) : Parcelable { // Tambahkan : Parcelable

    // Override ini diperlukan agar perbandingan objek yang mengandung ByteArray bekerja
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