package usecase

import dao.DaoEscrito
import entity.Error
import entity.Escrito
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.util.*

class EscritoUseCase(val daoEscrito: DaoEscrito){

    fun darEscritosPorProblematica(idProblematica: Int) = daoEscrito.darEscritosPorProblematica(idProblematica)

    fun agregarEscrito(escrito: Escrito, idPersonaProblematica: String) = daoEscrito.agregarEscrito(escrito, idPersonaProblematica)

    fun editarEscrito(escrito: Escrito, idPersonaProblematica: String, idEscrito: String): Any{
        val escritoActualizado = daoEscrito.editarEscrito(escrito, idPersonaProblematica, idEscrito)
        return escritoActualizado ?: Error(arrayOf("No se pudo actualizar el escrito, por favor verifique los parametros."))
    }

//    fun darEscrito(idProblematica: Int, idGrupo: Int, email: String): Optional<Escrito>
//         = daoEscrito.darEscrito("$email$idProblematica", idGrupo)

    fun darEscritosPorPersona(idPersonaProblematica: String)
        = daoEscrito.darEscritos(idPersonaProblematica)
}