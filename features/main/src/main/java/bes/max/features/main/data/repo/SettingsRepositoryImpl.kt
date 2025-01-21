package bes.max.features.main.data.repo

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import bes.max.features.main.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val DARK_THEME_PREFERENCES_KEY = booleanPreferencesKey("dark_theme_preference_key")
private val PIN_CODE_PREFERENCES_KEY = intPreferencesKey("pin_code_preference_key")
private const val TAG = "SettingsRepositoryImpl"

class SettingsRepositoryImpl(
    private val context: Context,
    private val preferencesDataStore: DataStore<Preferences>
) : SettingsRepository {
    override fun isNightModeActive(): Flow<Boolean> {
        return preferencesDataStore.data
            .catch { exception ->
                Log.e(
                    TAG,
                    "Error during getting DataStore: $exception"
                )
            }
            .map { preferences ->
                preferences[DARK_THEME_PREFERENCES_KEY] ?: isNightModeActiveDefault()
            }
    }

    override suspend fun setIsNightModeActive(isNightModeActive: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[DARK_THEME_PREFERENCES_KEY] = isNightModeActive
        }
    }

    override suspend fun setPinCode(pinCode: Int) {
        preferencesDataStore.edit { preferences ->
            preferences[PIN_CODE_PREFERENCES_KEY] = pinCode
        }
    }

    override suspend fun resetPinCode() {
        preferencesDataStore.edit { preferences ->
            preferences.remove(PIN_CODE_PREFERENCES_KEY)
        }
    }

    override fun pinCodeIsUsed(): Flow<Boolean> {
        return preferencesDataStore.data
            .catch { exception ->
                Log.e(
                    TAG,
                    "Error during getting DataStore: $exception"
                )
            }
            .map { preferences ->
                preferences[PIN_CODE_PREFERENCES_KEY] != null
            }
    }

    override fun pinCode(): Flow<Int?> {
        return preferencesDataStore.data
            .catch { exception ->
                Log.e(
                    TAG,
                    "Error during getting DataStore: $exception"
                )
            }
            .map { preferences ->
                preferences[PIN_CODE_PREFERENCES_KEY]
            }
    }

    private fun isNightModeActiveDefault(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.resources.configuration.isNightModeActive
        } else {
            false
        }
    }
}
