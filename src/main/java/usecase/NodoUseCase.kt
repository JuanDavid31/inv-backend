package usecase

import dao.DaoNodo
import entity.Mensaje
import entity.Error

class NodoUseCase(val daoNodo: DaoNodo) {

    fun apadrinar(id: Int, idPadre: Int): Any {
        val apadrinado = daoNodo.apadrinar(id, idPadre)
        return if (apadrinado) Mensaje("Conexión exitosa") else Error(arrayOf("El nodo no existe."))
    }

    fun desApadrinar(id: Int): Any {
        val conexionesEliminadas = daoNodo.eliminarConexionesPadreEHijo(id)
        return if(conexionesEliminadas) Mensaje("Conexiones eliminadas exitosamente") else Error(arrayOf("El nodo no existe o no tiene conexiones."))
    }
}