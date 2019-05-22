package usecase

import dao.DaoReaccion
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class ReaccionUseCase(val daoReaccion: DaoReaccion){

    fun reaccionar(valor: Int, idGrupo: Int, idPersonaProblematica: String) = daoReaccion.reaccionar(valor, idGrupo, idPersonaProblematica)

    fun eliminarReaccion(idGrupo: Int, idPersonaProblematica: String): Boolean{
        return try {
            daoReaccion.eliminarReaccion(idGrupo, idPersonaProblematica)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace()
            return false
        }
    }
}