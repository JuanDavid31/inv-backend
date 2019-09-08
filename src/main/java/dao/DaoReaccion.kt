package dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class DaoReaccion(private val jdbi: Jdbi) {

    fun reaccionar(valor: Int, idGrupo: Int, idPersonaProblematica: String): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> {
            it.createUpdate("INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(:valor, :idGrupo, :idPersonaProblematica)")
                .bind("valor", valor)
                .bind("idGrupo", idGrupo)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .execute() > 0
        }
    }

    @Throws(UnableToExecuteStatementException::class)
    fun eliminarReaccion(idGrupo: Int, idPersonaProblematica: String): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> {
            it.createUpdate("DELETE FROM REACCION WHERE c_id_grupo = :idGrupo AND a_id_pers_prob = :idPersonaProblematica")
                .bind("idGrupo", idGrupo)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .execute() > 0
        }
    }
}