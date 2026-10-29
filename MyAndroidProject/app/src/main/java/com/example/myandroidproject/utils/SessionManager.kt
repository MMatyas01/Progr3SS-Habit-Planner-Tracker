package com.example.myandroidproject.utils

import android.content.Context

class SessionManager private constructor(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ÚJ: rememberMe átadása a mentéskor
    fun saveSession(
        accessToken: String?,
        refreshToken: String?,
        userId: Long?,
        email: String?,
        username: String?,
        rememberMe: Boolean
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_USER_ID, userId ?: NO_USER)
            putString(KEY_EMAIL, email)
            putString(KEY_USERNAME, username)
            putBoolean(KEY_REMEMBER_ME, rememberMe) // ÚJ
        }.apply()
    }

    fun clear() = prefs.edit().clear().apply()

    val accessToken: String? get() = prefs.getString(KEY_ACCESS_TOKEN, null)
    val refreshToken: String? get() = prefs.getString(KEY_REFRESH_TOKEN, null)
    val userId: Long get() = prefs.getLong(KEY_USER_ID, NO_USER)
    val email: String? get() = prefs.getString(KEY_EMAIL, null)
    val username: String? get() = prefs.getString(KEY_USERNAME, null)

    // Módosítva: Csak akkor vagyunk belépve, ha van token ÉS be volt jelölve a Remember me
    val isLoggedIn: Boolean get() = !accessToken.isNullOrEmpty() && prefs.getBoolean(KEY_REMEMBER_ME, false)

    companion object {
        private const val PREF_NAME = "login_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_USERNAME = "username"
        private const val KEY_REMEMBER_ME = "remember_me" // ÚJ
        private const val NO_USER = -1L

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager =
            instance ?: synchronized(this) {
                instance ?: SessionManager(context).also {
                    // Ha az app indul, és a Remember me NEM volt bejelölve, töröljük a munkamenetet
                    if (!it.prefs.getBoolean(KEY_REMEMBER_ME, false)) {
                        it.clear()
                    }
                    instance = it
                }
            }
    }
}