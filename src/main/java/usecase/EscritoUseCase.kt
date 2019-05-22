package usecase

import dao.DaoEscrito
import entity.Escrito
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class EscritoUseCase(val daoEscrito: DaoEscrito){

    fun darEscritosPorProblematica(idProblematica: Int) = daoEscrito.darEscritosPorProblematica(idProblematica)

    fun darEscritoPorPersona(idPersonaProblematica: String) = daoEscrito.darEscritoPorPersona(idPersonaProblematica)

    fun agregarEscrito(escrito: Escrito, idPersonaProblematica: String) = daoEscrito.agregarEscrito(escrito, idPersonaProblematica)

    fun editarEscrito(escrito: Escrito, idPersonaProblematica: String, idEscrito: String): Boolean{
        return try {
            daoEscrito.editarEscrito(escrito, idPersonaProblematica, idEscrito)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace()
            return false
        }
    }
}
