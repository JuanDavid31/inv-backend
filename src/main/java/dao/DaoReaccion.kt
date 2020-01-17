package dao

import entity.Reaccion
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.util.*

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
    fun eliminarReaccion(idGrupo: Int, idReaccion: Int): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> {
            it.createUpdate("DELETE FROM REACCION WHERE c_id_grupo = :idGrupo AND c_id = :idReaccion")
                .bind("idGrupo", idGrupo)
                .bind("idReaccion", idReaccion)
                .execute() > 0
        }
    }

    fun darReaccionesPorUsuario(idProblematica: Int, email: String):List<Reaccion> {
        return jdbi.withHandle<List<Reaccion>, Exception>{
            it.createQuery("SELECT C_VALOR, C_ID_GRUPO FROM REACCION WHERE " +
            "A_ID_PERS_PROB = :idPersonaProblematica")
            .bind("idPersonaProblematica", "$email$idProblematica")
            .mapToBean(Reaccion::class.java)
            .list()
        }
    }

    fun eliminarReaccionPorGrupoYUsuario(idGrupo: Int, idPersonaProblematica: String): Boolean {
        return jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("DELETE FROM REACCION WHERE c_id_grupo = :idGrupo AND a_id_pers_prob = :idPersonaProblematica")
                .bind("idGrupo", idGrupo)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .execute() > 0
        }
    }
}