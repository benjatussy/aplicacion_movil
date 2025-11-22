package com.miapp.xanokotlin

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit {
            putString("token", token)
        }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun saveRole(role: String) {
        prefs.edit {
            putString("role", role)
        }
    }

    fun getRole(): String? = prefs.getString("role", null)

    fun saveUserId(userId: Int) {
        prefs.edit {
            putInt("user_id", userId)
        }
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun clear() {
        prefs.edit {
            clear()
        }
    }
}
