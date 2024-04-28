import auth.Client
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import java.security.Security

fun main() {
    // Add BouncyCastle security provider so we can access its algorithms
    Security.addProvider(BouncyCastleFipsProvider())

    val authData = Client.getAuthToken()
//    println("Client auth token: ${authData.token}")
}
