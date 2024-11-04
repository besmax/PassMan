package bes.max.features.main.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun isSystemPinCodeType(): Flow<Boolean>

    fun setPinCodeType(isSystemPinCode: Boolean)

}