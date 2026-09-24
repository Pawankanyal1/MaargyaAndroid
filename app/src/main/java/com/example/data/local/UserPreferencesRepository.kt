package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "maargya_user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val BACKEND_URL = stringPreferencesKey("backend_url")
        val USER_LOGGED_IN = booleanPreferencesKey("user_logged_in")
    }

    val themeMode: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.THEME_MODE] ?: "SYSTEM"
        }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] ?: false
        }

    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.USER_LOGGED_IN] ?: true // Default to logged in for immediate explore or test
        }

    val backendUrl: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[Keys.BACKEND_URL] ?: BuildConfig.MAARGYA_API_URL
        }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setUserLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_LOGGED_IN] = loggedIn
        }
    }

    suspend fun setBackendUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.BACKEND_URL] = url
        }
    }
}
