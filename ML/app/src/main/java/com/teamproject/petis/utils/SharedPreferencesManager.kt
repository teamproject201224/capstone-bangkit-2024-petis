package com.teamproject.petis.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveUserName(name: String) {
        Log.d("SharedPreferencesManager", "Saving username: $name")
        prefs.edit().putString("USER_NAME", name).apply()
    }

    fun getUserName(): String {
        val userName = prefs.getString("USER_NAME", "User") ?: "User"
        Log.d("SharedPreferencesManager", "Retrieved username: $userName")
        return userName
    }

    fun clearUserData() {
        // Gunakan prefs.edit() bukan sharedPreferences.edit()
        prefs.edit().clear().apply()
        Log.d("SharedPreferencesManager", "User data cleared")
    }

    // Metode tambahan opsional
    fun saveUserEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString("USER_EMAIL", null)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean("IS_LOGGED_IN", isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("IS_LOGGED_IN", false)
    }

    fun saveUserGender(gender: String) {
        prefs.edit().putString("user_gender", gender).apply()
    }

    fun getUserGender(): String {
        return prefs.getString("user_gender", "male") ?: "male"
    }
}