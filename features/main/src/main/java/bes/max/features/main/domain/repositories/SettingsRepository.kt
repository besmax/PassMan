package bes.max.features.main.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun isNightModeActive(): Flow<Boolean>

    suspend fun setIsNightModeActive(isNightModeActive: Boolean)

    suspend fun setPinCode(pinCode: Int)

    suspend fun resetPinCode()

    fun pinCodeIsUsed(): Flow<Boolean>

    fun pinCode(): Flow<Int?>

}