package usecase

import dao.DaoNodo
import entity.Error
import entity.Nodo
import util.FotoUtils

import java.io.IOException
import java.io.InputStream

class FotoUseCase(private val daoNodo: DaoNodo) {

    fun guardarFoto(nodo: Nodo, foto: InputStream, extensionFoto: String): Nodo? {
        if (!FotoUtils.extensionValida(extensionFoto)) return null
        try {
            nodo.id = daoNodo.agregarNodo(nodo)
            nodo.urlFoto = FotoUtils.guardarFotoEnDirectorioYDarUrl(nodo, foto, extensionFoto)
            nodo.rutaFoto = FotoUtils.darRuta(nodo, extensionFoto)
            daoNodo.actualizarNodo(nodo)
            return nodo
        } catch (e: IOException) {
            e.printStackTrace()
            if (nodo.id == 0) daoNodo.eliminarNodo(nodo.id)
            return null
        }
    }

    fun eliminarNodoYFoto(idNodo: Int): Any {
        val nodo = daoNodo.eliminarNodo(idNodo)
        return if(nodo != null) FotoUtils.eliminarFoto(nodo.rutaFoto) else Error(arrayOf("No existe el nodo"))
    }
}