package auth

import utils.deriveWithPbkdf2
import java.util.*

object Client {
    private val scanner = Scanner(System.`in`)

    fun getAuthToken(): ClientAuthData {
//        println("username:")
//        val username = scanner.nextLine().trim()
//
//        println("password:")
//        val password = scanner.nextLine().trim()

        val username = scanner.nextLine().trim()

        return ClientAuthData(
            username = "franco",
            password = "123",
            token = "123".deriveWithPbkdf2(),
        )
    }
}
