package com.example.ezdrive.model

data class Alamat(
    val id_user: Int,
    val nama_penerima: String,
    val no_hp_penerima: String,
    val jalan: String,
    val rt: String? = null,
    val rw: String? = null,
    val desa_kelurahan: String,
    val kecamatan: String,
    val kota_kabupaten: String,
    val provinsi: String,
    val kode_pos: String,
    val catatan: String? = null
)
