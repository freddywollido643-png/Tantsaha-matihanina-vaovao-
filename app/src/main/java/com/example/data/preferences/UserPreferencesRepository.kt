package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tantsaha_settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_REGION = stringPreferencesKey("selected_region")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val PRIMARY_ACTIVITY = stringPreferencesKey("primary_activity")
    }

    val selectedRegion: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_REGION] ?: "Antananarivo"
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: "Tantsaha"
    }

    val primaryActivity: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PRIMARY_ACTIVITY] ?: "Fiompiana & Fambolena"
    }

    suspend fun saveRegion(region: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_REGION] = region
        }
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun savePrimaryActivity(activity: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIMARY_ACTIVITY] = activity
        }
    }
}
