package auth

data class ClientAuthData(
    val username: String,
    val pbKdf2Token: String,
    val timestamp: Long,
)
