package auth

class ClientAuthData(
    val username: String,
    val password: String,
    val pbKdf2Token: String,
    val timestamp: Long,
)
