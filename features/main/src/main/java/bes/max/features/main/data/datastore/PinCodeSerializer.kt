package bes.max.features.main.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import bes.max.features.main.proto.PinCodeModel
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object PinCodeSerializer : Serializer<PinCodeModel> {
    override val defaultValue: PinCodeModel = PinCodeModel.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PinCodeModel = withContext(Dispatchers.IO) {
        return@withContext try {
            PinCodeModel.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: PinCodeModel, output: OutputStream) =
        withContext(Dispatchers.IO) { t.writeTo(output) }
}