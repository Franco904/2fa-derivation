import auth.Client
import auth.ClientAuthData
import auth.Server
import it.auties.qr.QrTerminal
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import java.security.Security
import java.util.*

fun main() {
    // Add BouncyCastle security provider so we can access its algorithms
    Security.addProvider(BouncyCastleFipsProvider())

    while (true) {
        when (getAppMode()) {
            "1" -> runUserSignUpMode()
            "2" -> runAuthMode()
            "0" -> break
        }
    }
}

private fun getAppMode(): String {
    println("=====")
    println("Escolha um modo de uso:")
    println("[ 1 ] Cadastro de usuário")
    println("[ 2 ] Autenticação de usuário")
    println("[ 0 ] Sair")
    println("=====")

    return Scanner(System.`in`).nextLine().trim()
}

private fun runUserSignUpMode() {
    println("[ Cadastro de usuário ]\n")

    val clientAuthData = Client.inputUsernamePassword()

    try {
        Server.signUpClient(clientAuthData = clientAuthData)
    } catch (e: Exception) {
        println(e.message)
    }
}

private fun runAuthMode() {
    println("[ Autenticação de usuário ]\n")

    // Username and password auth (first factor)
    val clientAuthData = Client.inputUsernamePassword()

    val password = try {
        Server.validateUsernamePassword(clientAuthData = clientAuthData)
    } catch (e: Exception) {
        return println(e.message)
    }

    // TOTP auth (second factor)
    val (serverTOTP, qrCodeMatrix) = Server.create2FACode(secret = password)
    QrTerminal.print(qrCodeMatrix, false)

    val clientTOTP = Client.input2FA()

    try {
        Server.validate2FACode(clientTOTP, serverTOTP)
    } catch (e: Exception) {
        return println(e.message)
    }

    // Create same session key for both client & server from TOTP code
    Server.derivateTOTPtoSessionKey(serverTOTP, clientAuthData)
    Client.derivateTOTPtoSessionKey(clientTOTP, clientAuthData)

    executeMessagingMode(clientAuthData)
}

private fun executeMessagingMode(clientAuthData: ClientAuthData) {
    while (true) {
        val clientMessage = Client.inputMessageToServer(clientAuthData)

        val serverResponse = Server.receiveMessageAndReply(clientMessage, clientAuthData)

        Client.readServerResponse(serverResponse, clientAuthData)

        if (!Client.continueConversation()) {
            // Exit
            break
        }
    }
}
