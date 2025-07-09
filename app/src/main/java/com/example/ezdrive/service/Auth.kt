package com.example.ezdrive.service

import android.content.Context
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Alamat
import com.example.ezdrive.model.User

fun handleLogin(context: Context, email: String, password: String, onResult: (Boolean, String) -> Unit) {
    // Tidak ada lagi pengecekan khusus untuk admin di sini.
    // Semua login diverifikasi melalui database.
    val db = DBHelper(context)
    val user = db.login(email, password)

    if (user != null) {
        // Jika user ditemukan di database, kirim role-nya
        onResult(true, user.role)
    } else {
        // Jika tidak ditemukan, gagal
        onResult(false, "")
    }
}

fun handleRegister(
    context: Context,
    fullName: String,
    email: String,
    password: String,
    onResult: (Boolean, String) -> Unit
) {
    val db = DBHelper(context)

    if (db.isEmailExists(email)) {
        onResult(false, "Email sudah terdaftar")
        return
    }

    // Tentukan role dengan memeriksa apakah email mengandung "@ezedrive"
    val role = if (email.contains("@ezdrive.com", ignoreCase = true)) {
        "admin"
    } else {
        "user"
    }

    val user = User(
        id = 0,
        username = fullName,
        email = email,
        password = password,
        alamat = null,
        no_hp = null,
        tanggal_lahir = null,
        role = role // Gunakan variabel role yang sudah ditentukan
    )

    val inserted = db.registerUser(user)

    if (inserted) {
        onResult(true, "Registrasi berhasil")
    } else {
        onResult(false, "Gagal mendaftarkan pengguna")
    }
}



