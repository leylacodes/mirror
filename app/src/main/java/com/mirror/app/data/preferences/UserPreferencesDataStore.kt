package com.mirror.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val AVATAR_TYPE = stringPreferencesKey("avatar_type")
        val NOTIF_HOUR = intPreferencesKey("notif_hour")
        val NOTIF_MINUTE = intPreferencesKey("notif_minute")
        val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            onboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
            avatarType = prefs[Keys.AVATAR_TYPE]?.let {
                runCatching { AvatarType.valueOf(it) }.getOrNull()
            } ?: AvatarType.TREE,
            notifHour = prefs[Keys.NOTIF_HOUR] ?: 20,
            notifMinute = prefs[Keys.NOTIF_MINUTE] ?: 0,
            notifEnabled = prefs[Keys.NOTIF_ENABLED] ?: false
        )
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setAvatarType(type: AvatarType) {
        context.dataStore.edit { it[Keys.AVATAR_TYPE] = type.name }
    }

    suspend fun setNotifTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.NOTIF_HOUR] = hour
            it[Keys.NOTIF_MINUTE] = minute
        }
    }

    suspend fun setNotifEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIF_ENABLED] = enabled }
    }

}
