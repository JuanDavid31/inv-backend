package usecase

import dao.DaoEscrito
import entity.Error
import entity.Escrito
import entity.Mensaje
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.util.*

class EscritoUseCase(val daoEscrito: DaoEscrito){

    fun agregarEscrito(escrito: Escrito, idPersonaProblematica: String): Any{
        val escrito = daoEscrito.agregarEscrito(escrito, idPersonaProblematica)
        return escrito ?: Error(arrayOf("No se pudo agregar el escrito, rectifique los parametros."))
    }

    fun editarEscrito(escrito: Escrito, idPersonaProblematica: String, idEscrito: Int): Any{
        val escritoActualizado = daoEscrito.editarEscrito(escrito, idPersonaProblematica, idEscrito)
        return escritoActualizado ?: Error(arrayOf("No se pudo actualizar el escrito, por favor verifique los parametros."))
    }

    fun darEscritosPorPersona(idPersonaProblematica: String)
        = daoEscrito.darEscritos(idPersonaProblematica)

    fun eliminarEscrito(idPersonaProblematica: String, idEscrito: Int): Any {
        val seElimino: Boolean = daoEscrito.eliminarEscrito(idPersonaProblematica, idEscrito)
        return if(seElimino) Mensaje("Escrito eliminado correctamente") else Error(arrayOf("El escrito no se pudo eliminar, verifique los parametros ingresados."))
    }
}