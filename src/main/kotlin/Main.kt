import auth.Client
import auth.Server
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import utils.getSalt
import java.security.Security
import java.util.*

/**
 * Roadmap: [link](https://docs.google.com/document/d/13e7_BFddyd-tu1rLySkT9ymcjwU2zJCA93OH2jPWwJM/edit)
 */
fun main() {
    // Add BouncyCastle security provider so we can access its algorithms
    Security.addProvider(BouncyCastleFipsProvider())

    when (getAppMode()) {
        "1" -> executeCadastroUsuarioMode()
        "2" -> executeAutenticacaoMode()
    }
}

private fun getAppMode(): String {
    println("=====")
    println("Escolha um modo de uso:")
    println("[ 1 ] Cadastro de usuário")
    println("[ 2 ] Autenticação de usuário")
    println("=====")

    return Scanner(System.`in`).nextLine().trim()
}

private fun executeCadastroUsuarioMode() {
    println("[ Cadastro de usuário ]\n")

    // TODO: Persistir credenciais do cliente
}

private fun executeAutenticacaoMode() {
    println("[ Autenticação de usuário ]\n")

    val salt = getSalt()
    val clientAuthData = Client.getAuthData(salt = salt)

    println("Client password: ${clientAuthData.password}")
    println("Client auth token (PBKDF2): ${clientAuthData.pbKdf2Token}")

    // TODO: Buscar token Scrypt do cliente persistido
    // TODO: Comparar tokens e retornar o resultado da primeira autenticação
    val scryptToken = Server.executeFirstClientAuth(clientAuthData, salt = salt)
    println("Server auth token (Scrypt): $scryptToken")
}
