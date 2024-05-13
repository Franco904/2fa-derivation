package auth

import com.google.zxing.client.j2se.MatrixToImageWriter
import utils.*
import java.io.File
import java.io.FileOutputStream

object Server {
    private var sessionKey = ""

    fun signUpClient(clientAuthData: ClientAuthData) {
        val file = File(resourcesFolder, "registry.txt").apply { createIfNotExists() }
        if (file.hasLine(clientAuthData.username)) throw Exception("Usuário já registrado.")

        val salt = getSaltForUser(clientAuthData.username)
        val scryptToken = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray())
        val scryptForKey = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 16) // AES KEY 128 bits
        val scryptForIv = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 12) // GCM IV 96 bits

        val secretKey = generateSecretKey(scryptForKey.toByteArray())
        val iv = generateIv(scryptForIv.toByteArray())

        file.putLine("${clientAuthData.username}=${scryptToken.encrypt(secretKey, iv)}")
        println("Usuário registrado com sucesso. Timestamp: ${clientAuthData.timestamp}")
    }

    fun validateUsernamePassword(clientAuthData: ClientAuthData): String {
        val file = File(resourcesFolder, "registry.txt").apply { createIfNotExists() }
        val scryptTokenStored = file.getLine(clientAuthData.username)?.split("=")?.get(1) ?: throw Exception("Usuário ou senha incorretos.")

        val salt = getSaltForUser(clientAuthData.username)
        val scryptToken = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray())
        val scryptForKey = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 16) // AES KEY 128 bits
        val scryptForIv = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 12) // GCM IV 96 bits

        val secretKey = generateSecretKey(scryptForKey.toByteArray())
        val iv = generateIv(scryptForIv.toByteArray())

        val decryptedScryptToken = scryptTokenStored.decrypt(secretKey, iv)

        if (scryptToken != decryptedScryptToken) throw Exception("Usuário ou senha incorretos.")

        println("Usuário autenticado com sucesso. Timestamp: ${clientAuthData.timestamp}")
        return decryptedScryptToken
    }

    fun create2FACode(secret: String): String {
        val totpToken = generateTotp(secret)
        val qrCodeMatrix = createQRCode(content = "https://large-type.com/#$totpToken")

        // Create QR Code with TOTP token
        val file = File(resourcesFolder, "qr_code.png")
        FileOutputStream(file).use { stream ->
            MatrixToImageWriter.writeToStream(qrCodeMatrix, "png", stream)
        }

        return totpToken
    }

    fun validate2FACode(clientTOTP: String, originalTOTP: String) {
        if (clientTOTP != originalTOTP) throw Exception("Código 2FA incorreto.")

        println("Usuário autenticado com sucesso.")
    }

    fun derivateTOTPtoSessionKey(totp: String, clientAuthData: ClientAuthData) {
        val salt = getSaltForUser(clientAuthData.username)

        val totpKey = totp.deriveWithPbkdf2(salt.toByteArray())
        sessionKey = totpKey
    }

    fun receiveMessageAndReply(message: String, clientAuthData: ClientAuthData): String {
        val salt = getSaltForUser(clientAuthData.username)

        val scryptForIv = clientAuthData.pbKdf2Token.deriveWithScrypt(salt.toByteArray(), keySize = 12) // GCM IV 96 bits
        val iv = generateIv(scryptForIv.toByteArray())

        if (sessionKey == "") throw Exception("Chave de sessão indisponível.")
        val secretKey = generateSecretKey(sessionKey.toByteArray())

        val decryptedMessage = message.decrypt(secretKey, iv)
        println("[ Servidor ] Recebeu a mensagem do cliente: $decryptedMessage")

        val serverReply = "( OK ) Código da mensagem: ${message.hashCode()}"
        return serverReply.encrypt(secretKey, iv)
    }
}
