package util

import entity.Nodo

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

object FotoUtils {

    private val DIRECTORIO = "/home/nodos"
    private val DOMINIO = "http://localhost:8081/nodos"

    @Throws(IOException::class)
    fun guardarFotoEnDirectorioYDarUrl(nuevoNodo: Nodo, foto: InputStream, extensionFoto: String): String {
        val rutaArchivo = crearDirectorioYDarRutaArchivo(nuevoNodo, extensionFoto)
        Files.copy(foto, Paths.get(DIRECTORIO + rutaArchivo))
        return DOMINIO + rutaArchivo
    }

    private fun crearDirectorioYDarRutaArchivo(nodo: Nodo, extension: String): String {
        val rutaArchivo = darRuta(nodo, extension)
        val ruta = DIRECTORIO + rutaArchivo
        val rutaFile = File(ruta)
        if (rutaFile.exists()) rutaFile.delete()
        rutaFile.parentFile.mkdirs()

        return rutaArchivo
    }

    fun darRuta(nodo: Nodo, extension: String): String {
        return "/" + nodo.idProblematica + "/" + nodo.email + "/" + nodo.id + "." + extension
    }

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
