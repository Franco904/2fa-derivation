package utils

import org.bouncycastle.util.encoders.Hex
import java.io.File
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// PBDKF2
fun String.deriveWithPbkdf2(): String {
    val pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS")

    val saltFile = File("src/main/resources/salt.txt")
    val salt = saltFile.inputStream().bufferedReader().readLines().first()

    val keySpec = PBEKeySpec(this.toCharArray(), salt.toByteArray(), 1000, 128)

    // Generate derived key from key spec
    val derivedKey = pbkdf2.generateSecret(keySpec)
    return Hex.toHexString(derivedKey.encoded)
}

// Scrypt
fun String.deriveWithScrypt(): String = ""
