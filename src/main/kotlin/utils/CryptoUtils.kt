package utils

import org.apache.commons.codec.binary.Hex
import java.io.File
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val ALGORITHM = "AES"
private const val BLOCK_MODE = "GCM"
private const val PADDING_SCHEME = "NoPadding"
private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING_SCHEME"
private const val PROVIDER = "BCFIPS"

private val encripter = Cipher.getInstance(TRANSFORMATION, PROVIDER).apply {
    init(Cipher.ENCRYPT_MODE, getSecretKey())
}

private fun getDecrypter(iv: ByteArray) = Cipher.getInstance(TRANSFORMATION, PROVIDER).apply {
    init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
}

private fun getSecretKey(): SecretKey {
    val file = File("src/main/resources/secret_key.txt").apply { createIfNotExists() }

    val keyHex = file.getFirstLineOrNull().let {
        it ?: generateSecretKey().also { key -> file.putLine(key) }
    }

    return SecretKeySpec(keyHex.toByteArray(), ALGORITHM)
}

private fun generateSecretKey(): String {
    val key = KeyGenerator.getInstance(ALGORITHM, PROVIDER).generateKey()
    return Hex.encodeHexString(key.encoded)
}

fun String.encrypt(): String {
    val encryptedTextBytes = encripter.doFinal(this.toByteArray())
    val encryptedTextHex = Hex.encodeHexString(encryptedTextBytes)

    val ivBytes = encripter.iv
    val ivHex = Hex.encodeHexString(ivBytes)

    // TODO: Rever esse formato de retorno aqui!!
    return "$ivHex:$encryptedTextHex"
}

fun String.decrypt(): String {
    val rawText = this.split(":")
    val (ivHex, encryptedTextHex) = rawText[0] to rawText[1]

    val ivBytes = Hex.decodeHex(ivHex)
    val encryptedTextBytes = Hex.decodeHex(encryptedTextHex)

    val decrypter = getDecrypter(ivBytes)
    val decryptedTextBytes = decrypter.doFinal(encryptedTextBytes)

    return String(decryptedTextBytes, Charsets.UTF_8)
}
