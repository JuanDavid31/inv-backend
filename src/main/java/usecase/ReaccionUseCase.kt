package usecase

import dao.DaoReaccion
import entity.Reaccion
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class ReaccionUseCase(val daoReaccion: DaoReaccion){

    fun reaccionar(reaccion: Reaccion, idGrupo: Int, idPersonaProblematica: String): Reaccion {
        //Si solo hiciera una consulta para conocer su existencia, entonces tendría que hacer una segunda consulta(Sea a la db o como servicio)
        //para la eliminación
        daoReaccion.eliminarReaccionPorGrupoYUsuario(idGrupo, idPersonaProblematica)
        return daoReaccion.reaccionar(reaccion, idGrupo, idPersonaProblematica)
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