package bes.max.features.main.data.datastore

import bes.max.features.main.domain.models.PinCodeModelMain
import bes.max.features.main.proto.PinCodeModel

fun PinCodeModel.map() = PinCodeModelMain(
    pinCode = pincode,
    active = active,
    iv = iv,
)