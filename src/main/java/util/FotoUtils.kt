package util

import entity.Nodo

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

class FotoUtils(val ip:String) {

    private val DIRECTORIO = "/home/nodos"
    private val DOMINIO = "http://${ip}/nodos"

    fun eliminarFoto(rutaFoto: String): Boolean {
        return File(DIRECTORIO + rutaFoto).delete()
    }

    fun extensionValida(extensionFoto: String): Boolean {
        if (extensionFoto.contains(".")) return false
        return when (extensionFoto.toUpperCase()) {
            "PNG", "JPG", "JPEG" -> true
            else -> false
        }
    }
}
