package usecase

import dao.DaoNodo
import entity.Error
import entity.Nodo
import util.FotoUtils

import java.io.IOException
import java.io.InputStream

class FotoUseCase(private val daoNodo: DaoNodo, private val fotoUtils: FotoUtils) {

    fun guardarFoto(nodo: Nodo, foto: InputStream, extensionFoto: String): Nodo? {
        if (!fotoUtils.extensionValida(extensionFoto)) return null
        try {
            nodo.id = daoNodo.agregarNodo(nodo)
            if(nodo.id == 0) return null
            nodo.urlFoto = fotoUtils.guardarFotoEnDirectorioYDarUrl(nodo, foto, extensionFoto) //Lanza la excepción
            nodo.rutaFoto = fotoUtils.darRuta(nodo, extensionFoto)
            daoNodo.actualizarNodo(nodo)
            return nodo
        } catch (e: IOException) {
            e.printStackTrace()
            daoNodo.eliminarNodo(nodo.id)
            return null
        }
    }

    fun eliminarNodoYFoto(idNodo: Int): Any {
        val nodo = daoNodo.eliminarNodo(idNodo)
        return if(nodo != null) fotoUtils.eliminarFoto(nodo.rutaFoto) else Error(arrayOf("No existe el nodo"))
    }
}