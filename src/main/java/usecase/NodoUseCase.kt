package usecase

import dao.DaoNodo
import entity.Nodo
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class NodoUseCase(val daoNodo: DaoNodo) {

    fun darNodos(idPersonaProblematica: String) = daoNodo.darNodos(idPersonaProblematica)

    fun agregarNodo(nodo: Nodo) = daoNodo.agregarNodo(nodo)

    fun actualizarNodo(nodo: Nodo): Boolean {
        return try {
            daoNodo.actualizarNodo(nodo)
        } catch (e: UnableToExecuteStatementException) {
            e.printStackTrace()
            return false
        }
    }

    fun apadrinar(id: Int, idPadre: Int): Boolean {
        return try {
            daoNodo.apadrinar(id, idPadre)
        } catch (e: UnableToExecuteStatementException) {
            e.printStackTrace()
            return false
        }
    }

    fun desApadrinar(id: Int): Boolean {
        return try {
            daoNodo.desApadrinar(id)
        } catch (e: UnableToExecuteStatementException) {
            e.printStackTrace()
            return false
        }
    }

    fun eliminarNodo(id: Int): Nodo? {
        return try {
            daoNodo.eliminarNodo(id)
        } catch (e: UnableToExecuteStatementException) {
            e.printStackTrace()
            return null
        }
    }
}