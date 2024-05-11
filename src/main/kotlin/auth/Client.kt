package auth

import utils.*
import java.net.CacheResponse
import java.util.*

object Client {
    private val scanner = Scanner(System.`in`)
    private var sessionKey = ""

    fun inputUsernamePassword(): ClientAuthData {
        println("Nome do usuário:")
        val username = scanner.nextLine().trim()

        println("Senha do usuário:")
        val password = scanner.nextLine().trim()

        val salt = getSaltForUser(username)
        val pbkdf2Token = password.deriveWithPbkdf2(salt.toByteArray())

        return ClientAuthData(
            username = username,
            password = password,
            pbKdf2Token = pbkdf2Token,
            timestamp = System.currentTimeMillis(),
        )
    }

    fun input2FA(): String {
        println("Código 2FA:")
        val totpCode = Scanner(System.`in`).nextLine().trim()
        return totpCode
    }

    fun derivateTOTPtoSessionKey(totp: String, clientAuthData: ClientAuthData) {
        val salt = getSaltForUser(clientAuthData.username)
        val totpKey = totp.deriveWithPbkdf2(salt.toByteArray())
        sessionKey = totpKey
    }

    fun inputMessageToServer(clientAuthData: ClientAuthData) : String {
        println("Mensagem:")
        val message = scanner.nextLine().trim()

        val salt = getSaltForUser(clientAuthData.username)

        val scryptForIv = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 12) // GCM IV 96 bits
        val iv = generateIv(scryptForIv.toByteArray())

        if (sessionKey.isEmpty()) throw Exception("Chave de sessão indisponível.")
        val secretKey = generateSecretKey(sessionKey.toByteArray())

        return message.encrypt(secretKey, iv)
    }

    fun readServerResponse(serverResponse: String, clientAuthData: ClientAuthData) {
        val salt = getSaltForUser(clientAuthData.username)

        val scryptForIv = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 12) // GCM IV 96 bits
        val iv = generateIv(scryptForIv.toByteArray())

        if (sessionKey.isEmpty()) throw Exception("Chave de sessão indisponível.")
        val secretKey = generateSecretKey(sessionKey.toByteArray())

        val decryptedMessage = serverResponse.decrypt(secretKey, iv)

        println("[ Cliente ] Recebeu a resposta do servidor: $decryptedMessage")
    }

    fun continueConversation(): Boolean {
        println("Nova mensagem? (s/n):")
        val confirmation = scanner.nextLine().trim()

        return confirmation.lowercase() == "s"
    }
}
