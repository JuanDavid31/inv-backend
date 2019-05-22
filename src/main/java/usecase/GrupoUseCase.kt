package usecase

import dao.DaoGrupo
import entity.Grupo
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class GrupoUseCase(val daoGrupo: DaoGrupo){

    fun darGrupos(idProblematica: Int) = daoGrupo.darGrupos(idProblematica)

    fun agregarGrupo(idProblematica: Int, grupo: Grupo) = daoGrupo.agregarGrupo(idProblematica, grupo)

    fun actualizarGrupo(idGrupo: Int, grupo: Grupo): Boolean{
        return try {
            daoGrupo.actualizarGrupo(idGrupo, grupo)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace()
            return false
        }
    }

    fun apadrinar(id: Int, idPadre: Int, idProblematica: Int): Boolean{
        return try {
            daoGrupo.apadrinar(id, idPadre, idProblematica)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace()
            return false
        }
    }

    fun desApadrinar(id: Int, idProblematica: Int): Boolean{
        return try {
            daoGrupo.desApadrinar(id, idProblematica)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace()
            return false
        }
    }

    fun darGruposConReacciones(idProblematica: Int) = daoGrupo.darGruposConReacciones(idProblematica)

    fun darGrupoConReaccion(idProblematica: Int, idPersonaProblematica: String): Grupo? {
        val optionalGrupo = daoGrupo.darGrupoConReaccion(idProblematica, idPersonaProblematica)
        return if (optionalGrupo.isPresent) optionalGrupo.get() else null
    }

    fun eliminarGrupo(id: Int, idProblematica: Int): Boolean {
        return try {
            daoGrupo.eliminarGrupo(id, idProblematica)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace()
            return false
        }
    }
}