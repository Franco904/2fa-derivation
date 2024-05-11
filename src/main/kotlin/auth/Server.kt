package auth

import org.apache.commons.codec.binary.Hex
import utils.*
import java.io.File

object Server {
    fun signUpClient(clientAuthData: ClientAuthData) = with(clientAuthData) {
        val file = File(resourcesFolder, "registry.txt").apply { createIfNotExists() }
        if (file.hasLine(username)) throw Exception("Username already registered")

        val salt = getSaltForUser(username)
        val scryptToken = pbKdf2Token.deriveWithScrypt(salt.toByteArray())
        val scryptForKey = pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 16) // AES KEY 128 bits
        val scryptForIv = pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 12) // GCM IV 96 bits

        val secretKey = generateSecretKey(scryptForKey.toByteArray())
        val iv = generateIv(scryptForIv.toByteArray())

        println("key encrypt: ${Hex.encodeHexString(secretKey.encoded)}")
        println("iv encrypt: ${Hex.encodeHexString(iv.iv)}")

        file.putLine("$username=${scryptToken.encrypt(secretKey, iv)}")
    }

    fun executeFirstClientAuth(clientAuthData: ClientAuthData): Boolean = with(clientAuthData) {
        val file = File(resourcesFolder, "registry.txt").apply { createIfNotExists() }
        val scryptTokenStored = file.getLine(username)?.split("=")?.get(1) ?: return false

        val salt = getSaltForUser(username)
        val scryptToken = pbKdf2Token.deriveWithScrypt(salt.toByteArray())
        val scryptForKey = pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 16) // AES KEY 128 bits
        val scryptForIv = pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 12) // GCM IV 96 bits

        val secretKey = generateSecretKey(scryptForKey.toByteArray())
        val iv = generateIv(scryptForIv.toByteArray())

        println("key decrypt: ${Hex.encodeHexString(secretKey.encoded)}")
        println("iv decrypt: ${Hex.encodeHexString(iv.iv)}")

        scryptToken == scryptTokenStored.decrypt(secretKey, iv)
    }
}
