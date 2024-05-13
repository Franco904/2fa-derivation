package utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import de.taimos.totp.TOTP
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex

private const val QR_CODE_WIDTH = 24
private const val QR_CODE_HEIGHT = 24

fun generateTotp(secret: String): String {
    val base32 = Base32()
    val bytes = base32.decode(secret.toByteArray())
    val hexKey = Hex.encodeHexString(bytes)

    return TOTP.getOTP(hexKey)
}

fun createQRCode(content: String): BitMatrix? {
    val matrix = MultiFormatWriter().encode(
        content, BarcodeFormat.QR_CODE,
        QR_CODE_WIDTH, QR_CODE_HEIGHT
    )
    return matrix
}
