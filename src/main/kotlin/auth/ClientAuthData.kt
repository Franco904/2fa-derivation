package auth

class ClientAuthData(
    val username: String,
    val password: String,
    val token: String, // derived password
)
