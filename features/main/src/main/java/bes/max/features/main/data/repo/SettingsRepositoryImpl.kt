package bes.max.features.main.data.repo

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import bes.max.features.main.data.datastore.map
import bes.max.features.main.domain.models.PinCodeModelMain
import bes.max.features.main.domain.repositories.SettingsRepository
import bes.max.features.main.proto.PinCodeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val DARK_THEME_PREFERENCES_KEY = booleanPreferencesKey("dark_theme_preference_key")
private val ANIM_BACKGROUND_PREFERENCES_KEY = booleanPreferencesKey("anim_background_preference_key")
private const val TAG = "SettingsRepositoryImpl"

class SettingsRepositoryImpl(
    private val context: Context,
    private val preferencesDataStore: DataStore<Preferences>,
    private val pinCodeDataStore: DataStore<PinCodeModel>,
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

    override fun isAnimBackgroundActive(): Flow<Boolean> {
        return preferencesDataStore.data
            .catch { exception ->
                Log.e(
                    TAG,
                    "Error during getting DataStore: $exception"
                )
            }
            .map { preferences ->
                preferences[ANIM_BACKGROUND_PREFERENCES_KEY] ?: false
            }
    }

    override suspend fun setIsAnimBackgroundActive(isAnimBackgroundActive: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[ANIM_BACKGROUND_PREFERENCES_KEY] = isAnimBackgroundActive
        }
    }

    override suspend fun setPinCode(pinCode: PinCodeModelMain) {
        pinCodeDataStore.updateData {
            it.toBuilder()
                .setPincode(pinCode.pinCode)
                .setActive(pinCode.active)
                .setIv(pinCode.iv)
                .build()
        }
    }

    override suspend fun resetPinCode() {
        pinCodeDataStore.updateData {
            it.toBuilder()
                .setActive(false)
                .build()
        }
    }

    override fun pinCodeIsUsed(): Flow<Boolean> {
        return pinCodeDataStore.data.map { it.active }
    }

    override fun pinCode(): Flow<PinCodeModelMain> {
        return pinCodeDataStore.data.map { it.map() }
    }

    private fun isNightModeActiveDefault(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.resources.configuration.isNightModeActive
        } else {
            false
        }
    }
}
