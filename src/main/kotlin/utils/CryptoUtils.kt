package utils

import org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val ALGORITHM = "AES"
private const val BLOCK_MODE = "GCM"
private const val PADDING_SCHEME = "NoPadding"
private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING_SCHEME"
private const val PROVIDER = "BCFIPS"

private fun getEncrypter(secretKey: SecretKey, iv: AlgorithmParameterSpec): Cipher {
    return Cipher.getInstance(TRANSFORMATION, PROVIDER).apply {
        init(Cipher.ENCRYPT_MODE, secretKey, iv)
    }
}

private fun getDecrypter(secretKey: SecretKey, iv: AlgorithmParameterSpec): Cipher {
    return Cipher.getInstance(TRANSFORMATION, PROVIDER).apply {
        init(Cipher.DECRYPT_MODE, secretKey, iv)
    }
}

fun generateSecretKey(keyBytes: ByteArray): SecretKey {
    val keySpec = SecretKeySpec(keyBytes, ALGORITHM)

    return SecretKeyFactory.getInstance(ALGORITHM, PROVIDER).generateSecret(keySpec)
}

fun generateIv(ivBytes: ByteArray) = IvParameterSpec(ivBytes)

fun String.encrypt(key: SecretKey, iv: AlgorithmParameterSpec): String {
    val encrypter = getEncrypter(key, iv)
    val encryptedTextBytes = encrypter.doFinal(this.toByteArray())

    return Hex.encodeHexString(encryptedTextBytes)
}

fun String.decrypt(key: SecretKey, iv: AlgorithmParameterSpec): String {
    val decrypter = getDecrypter(key, iv)

    val decryptedTextBytes = try {
        decrypter.doFinal(Hex.decodeHex(this))
    } catch (e: AEADBadTagException) {
        "IV não é o mesmo da cifragem".toByteArray()
    }

    return String(decryptedTextBytes, Charsets.UTF_8)
}
