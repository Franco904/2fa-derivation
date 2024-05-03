package auth

import utils.*
import java.util.*

object Client {
    private val scanner = Scanner(System.`in`)

    fun getAuthData(): ClientAuthData {
        println("username:")
        val username = scanner.nextLine().trim()

        println("password:")
        val password = scanner.nextLine().trim()

//        val username = "teste"
//        val password = "123123as"

        val salt = getSaltForUser(username)
        val pbkdf2Token = password.deriveWithPbkdf2(salt.toByteArray())

        return ClientAuthData(
            username = username,
            password = password,
            pbKdf2Token = pbkdf2Token,
            timestamp = System.currentTimeMillis(),
        )
    }
}
