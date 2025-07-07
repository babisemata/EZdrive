package com.example.ezdrive.service

import android.content.Context
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Alamat
import com.example.ezdrive.model.User

fun handleLogin(context: Context, email: String, password: String, onResult: (Boolean, String) -> Unit) {
    val db = DBHelper(context)

    val user = db.login(email, password)

    if (user != null) {
        onResult(true, user.role)
    } else {
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
        onResult(false, "Email already registered")
        return
    }

    val user = User(
        id = 0,
        username = fullName,
        email = email,
        password = password,
        alamat = null,
        no_hp = null,
        tanggal_lahir = null,
        role = "user"
    )

    val inserted = db.registerUser(user)

    if (inserted) {
        onResult(true, "Register successful")
    } else {
        onResult(false, "Failed to register user")
    }
}



