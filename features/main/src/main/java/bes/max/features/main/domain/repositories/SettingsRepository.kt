package bes.max.features.main.domain.repositories

import bes.max.features.main.domain.models.PinCodeModelMain
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun isNightModeActive(): Flow<Boolean>

    suspend fun setIsNightModeActive(isNightModeActive: Boolean)

    suspend fun setPinCode(pinCode: PinCodeModelMain)

    suspend fun resetPinCode()

    fun pinCodeIsUsed(): Flow<Boolean>

    fun pinCode(): Flow<PinCodeModelMain>

}