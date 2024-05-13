package auth

import com.google.zxing.common.BitMatrix

data class ServerTwoFAData(
    val totpToken: String,
    val qrCodeMatrix: BitMatrix?,
)
