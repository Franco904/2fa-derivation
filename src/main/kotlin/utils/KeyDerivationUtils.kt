package utils

import org.bouncycastle.crypto.fips.Scrypt
import org.bouncycastle.crypto.fips.Scrypt.KDFFactory
import org.bouncycastle.util.Strings
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val PBKDF2_ITERATION_COUNT = 1000
private const val PBKDF2_KEY_LENGTH = 128

private const val SCRYPT_COST_PARAM = 2048
private const val SCRYPT_BLOCKSIZE = 8
private const val SCRYPT_PARALLELIZATION_PARAM = 1

fun getSaltForUser(username: String): String {
    val file = File(resourcesFolder, "users-salt.txt").apply { createIfNotExists() }

    val saltFromFile = file.getLine(username)?.split("=")?.get(1)
    val saltHex = saltFromFile ?: generateSalt().also { salt -> file.putLine("$username=$salt") }

    return saltHex
}

private fun generateSalt(): String {
    val sr = SecureRandom.getInstance("SHA1PRNG")

    val saltBytes = ByteArray(16)
    sr.nextBytes(saltBytes)

    val salt = Hex.toHexString(saltBytes)
    return salt
}

// PBDKF2
fun String.deriveWithPbkdf2(salt: ByteArray): String {
    val key = this.toCharArray()

    val keySpec = PBEKeySpec(key, salt, PBKDF2_ITERATION_COUNT, PBKDF2_KEY_LENGTH)

    val pbKdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS")

    // Generate derived key from key spec
    val derivedKey = pbKdf2.generateSecret(keySpec)
    return Hex.toHexString(derivedKey.encoded)
}

// Scrypt
fun String.deriveWithScrypt(salt: ByteArray, keySize: Int = 32): String {
    val key = this.toCharArray()

    val params = Scrypt.ALGORITHM.using(
        salt,
        SCRYPT_COST_PARAM,
        SCRYPT_BLOCKSIZE,
        SCRYPT_PARALLELIZATION_PARAM,
        Strings.toUTF8ByteArray(key),
    )

    val scryptKdf = KDFFactory().createKDFCalculator(params)

    val derivedKeyBytes = ByteArray(keySize)
    scryptKdf.generateBytes(derivedKeyBytes)

    return Hex.toHexString(derivedKeyBytes)
}
