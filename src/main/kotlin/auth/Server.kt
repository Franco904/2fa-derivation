package auth

import utils.deriveWithScrypt

object Server {
    fun executeFirstClientAuth(
        clientAuthData: ClientAuthData,
        salt: ByteArray,
    ) = clientAuthData.pbKdf2Token.deriveWithScrypt(salt)
}
