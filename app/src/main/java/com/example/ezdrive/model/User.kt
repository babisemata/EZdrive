package com.example.ezdrive.model

data class User(
    val id: Int,
    val username: String?=null,
    val email: String,
    val password: String,
    val profilePicture: ByteArray? = null,
    val alamat: List<Alamat>?=null,
    val no_hp: String?=null,
    val tanggal_lahir: String?=null,
    val role: String,
    val alamatList: List<Alamat> = emptyList()
)
