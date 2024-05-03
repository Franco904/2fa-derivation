import auth.Client
import auth.Server
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import utils.generateAndWriteSalt
import java.security.Security
import java.util.*

/**
 * Roadmap: [link](https://docs.google.com/document/d/13e7_BFddyd-tu1rLySkT9ymcjwU2zJCA93OH2jPWwJM/edit)
 */
fun main() {
    // Add BouncyCastle security provider so we can access its algorithms
    Security.addProvider(BouncyCastleFipsProvider())

    generateAndWriteSalt()

    when (getAppMode()) {
        "1" -> executeUserSignUpMode()
        "2" -> executeAuthMode()
    }
//    deleteTextFiles()
}

private fun getAppMode(): String {
    println("=====")
    println("Escolha um modo de uso:")
    println("[ 1 ] Cadastro de usuário")
    println("[ 2 ] Autenticação de usuário")
    println("=====")

    return Scanner(System.`in`).nextLine().trim()
}

private fun executeUserSignUpMode() {
    println("[ Cadastro de usuário ]\n")
}

private fun executeAuthMode() {
    println("[ Autenticação de usuário ]\n")

    val clientAuthData = Client.getAuthData()

    println("Client password: ${clientAuthData.password}")
    println("Client auth token (PBKDF2): ${clientAuthData.pbKdf2Token}")

    val scryptToken = Server.executeFirstClientAuth(clientAuthData)
    println("Server auth token (Scrypt): $scryptToken")
private fun deleteTextFiles() {
    val resourcesFolder = File("src/main/resources")
    resourcesFolder.listFiles()?.forEach { file -> file.delete() }
}
