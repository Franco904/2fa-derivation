package auth

import utils.createIfNotExists
import utils.deriveWithScrypt
import utils.hasLine
import utils.putLine
import java.io.File

object Server {
    fun signUpClient(clientAuthData: ClientAuthData) {
        val username = clientAuthData.username
        val scryptToken = clientAuthData.pbKdf2Token.deriveWithScrypt()

        val file = File("src/main/resources/registry.txt").apply { createIfNotExists() }
        if (file.hasLine(username)) throw Exception("Username already registered")

        file.putLine("$username:$scryptToken")
    }

    fun executeFirstClientAuth(clientAuthData: ClientAuthData) = clientAuthData.pbKdf2Token.deriveWithScrypt()
}
