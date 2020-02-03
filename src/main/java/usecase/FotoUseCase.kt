package usecase

import dao.DaoNodo
import entity.Error
import entity.Nodo
import util.FotoUtils
import util.S3Utils

import java.io.IOException
import java.io.InputStream

class FotoUseCase(private val daoNodo: DaoNodo, private val fotoUtils: FotoUtils, private val s3Utils: S3Utils) {

    fun guardarFoto(nodo: Nodo, foto: InputStream, extensionFoto: String): Any {
        if (!fotoUtils.extensionValida(extensionFoto)) return Error(arrayOf("Solo imagenes con extensión .PNG, .JPG o .JPEG son validas"))
        try {
            nodo.id = daoNodo.agregarNodo(nodo)
            if(nodo.id == 0) return Error(arrayOf("No fue posible agregar el nodo, verifique los parametros."))
            nodo.urlFoto = s3Utils.cargarImagen(nodo, foto, extensionFoto)
            daoNodo.actualizarUrlNodo(nodo)
            return nodo
        } catch (e: IOException) {
            daoNodo.eliminarNodo(nodo.id)
            return Error(arrayOf("Un error ha ocurrido, intentelo de nuevo."))
        }
    }

    fun eliminarNodoYFoto(idNodo: Int): Any {
        val nodo = daoNodo.eliminarNodo(idNodo)
        return if(nodo != null) s3Utils.eliminarImagen(nodo) else Error(arrayOf("No existe el nodo"))
    }
}