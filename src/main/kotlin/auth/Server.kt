package auth

import utils.*
import java.io.File

object Server {
    fun signUpClient(clientAuthData: ClientAuthData) {
        val username = clientAuthData.username
        val salt = getSaltForUser(username)

        val scryptToken = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray())

        val file = File("src/main/resources/registry.txt").apply { createIfNotExists() }
        if (file.hasLine(username)) throw Exception("Username already registered")

        file.putLine("$username=${scryptToken.encrypt()}")
    }

    fun executeFirstClientAuth(clientAuthData: ClientAuthData): Boolean {
        val username = clientAuthData.username
        val salt = getSaltForUser(username)

        val scryptToken = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray())

        val file = File("src/main/resources/registry.txt").apply { createIfNotExists() }
        val scryptTokenStored = file.getLine(username)?.split("=")?.get(1)
        val decryptedToken = scryptTokenStored?.decrypt()

        return scryptToken == decryptedToken
    }
}
