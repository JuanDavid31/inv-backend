package usecase

import dao.DaoReaccion
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class ReaccionUseCase(val daoReaccion: DaoReaccion){

    fun reaccionar(valor: Int, idGrupo: Int, idPersonaProblematica: String): Boolean{
        //Si solo hiciera una consulta para conocer su existencia, entonces tendría que hacer una segunda consulta para la eliminación
        daoReaccion.eliminarReaccionPorGrupoYUsuario(idGrupo, idPersonaProblematica)
        return daoReaccion.reaccionar(valor, idGrupo, idPersonaProblematica)
    }

    fun eliminarReaccion(idGrupo: Int, idReaccion: Int): Boolean{
        return try {
            daoReaccion.eliminarReaccion(idGrupo, idReaccion)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace()
            return false
        }
    }
}