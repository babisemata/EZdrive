package com.example.ezdrive.service

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ezdrive_prefs", Context.MODE_PRIVATE)

    fun saveLogin(email: String, role: String) {
        prefs.edit {
            putBoolean("is_logged_in", true)
                .putString("email", email)
                .putString("role", role)
        }
    }

    fun clearSession() {
        prefs.edit { clear() }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getUserEmail(): String? {
        return prefs.getString("email", null)
    }

    fun getUserRole(): String? {
        return prefs.getString("role", null)
    }

}
