package util

class FotoUtils {

    /**
     * @param extensionFoto String limpio, posibles valores: PNG, JPG o JPEG.
     * No puede llevar puntos.
     */
    fun extensionValida(extensionFoto: String): Boolean {
        if (extensionFoto.contains(".")) return false
        return when (extensionFoto.toUpperCase()) {
            "PNG", "JPG", "JPEG" -> true
            else -> false
        }
    }
}
