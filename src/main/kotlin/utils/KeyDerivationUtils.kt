package utils

import constants.*
import org.bouncycastle.crypto.fips.Scrypt
import org.bouncycastle.crypto.fips.Scrypt.KDFFactory
import org.bouncycastle.util.Strings
import org.bouncycastle.util.encoders.Hex
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

fun getSalt(): ByteArray {
    val sr = SecureRandom.getInstance("SHA1PRNG")
    val saltBytes = ByteArray(16)
    sr.nextBytes(saltBytes)

    return saltBytes
}

// PBDKF2
fun String.deriveWithPbkdf2(salt: ByteArray): String {
    val key = this.toCharArray()

    val keySpec = PBEKeySpec(key, salt, pbkdf2IterationCount, pbkdf2KeyLength)

    val pbKdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS")

    // Generate derived key from key spec
    val derivedKey = pbKdf2.generateSecret(keySpec)
    return Hex.toHexString(derivedKey.encoded)
}

// Scrypt
fun String.deriveWithScrypt(salt: ByteArray): String {
    val key = this.toCharArray()

    val params = Scrypt.ALGORITHM.using(
        salt,
        scryptCostParameter,
        scryptBlocksize,
        scryptParallelizationParam,
        Strings.toUTF8ByteArray(key),
    )

    val scryptKdf = KDFFactory().createKDFCalculator(params)

    val derivedKeyBytes = ByteArray(32)
    scryptKdf.generateBytes(derivedKeyBytes)

    return Hex.toHexString(derivedKeyBytes)
}
