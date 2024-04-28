package auth

import utils.deriveWithScrypt

object Server {
    fun executeFirstClientAuth(clientAuthData: ClientAuthData) = clientAuthData.pbKdf2Token.deriveWithScrypt()
}
