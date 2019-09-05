package dao

import entity.Problematica
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.util.Optional

class DaoProblematica(internal val jdbi: Jdbi) {

    fun agregarProblematicaPorPersona(email: String, problematica: Problematica): Problematica {
        return jdbi.inTransaction<Problematica, RuntimeException> {
            try{
                val nuevaProblematica = it
                        .createUpdate("INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion, c_fase) VALUES(:nombre, :descripcion, now(), 0)")
                        .bindBean(problematica)
                        .executeAndReturnGeneratedKeys()
                        .mapToBean(Problematica::class.java)
                        .findOnly()
                val seAgrego = it.createUpdate("INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) " +
                        "VALUES(concat(:email, :id), :email, :id, :interventor)")
                        .bind("email", email)
                        .bind("interventor", true)
                        .bindBean(nuevaProblematica)
                        .execute() > 0
                if (!seAgrego) {
                    it.rollback()
                    null
                }
                nuevaProblematica
            }catch (e: UnableToExecuteStatementException){
                e.printStackTrace()
                it.rollback()
                null
            }
        }
    }

    fun darProblematicasPorPersona(email: String): List<Problematica> {
        return jdbi.withHandle<List<Problematica>, RuntimeException> {
            it.createQuery("SELECT DISTINCT P.c_id, P.a_nombre, P.a_descripcion, P.f_fecha_creacion, PP.b_interventor , PP.a_email P.c_fase " +
                    "FROM PROBLEMATICA P, PERSONA_PROBLEMATICA PP, PERSONA " +
                    "WHERE PERSONA.a_email = :email AND PERSONA.a_email = PP.a_email AND P.c_id = PP.c_id_problematica " +
                    "ORDER BY P.c_id")
                    .bind("email", email)
                    .mapToBean(Problematica::class.java)
                    .list()
        }
    }

    fun darFase(idProblematica: Int): Optional<Int> {
        return jdbi.withHandle<Optional<Int>, RuntimeException> { handle ->
            handle.createQuery("SELECT c_fase FROM PROBLEMATICA where c_id = :idProblematica")
                    .bind("idProblematica", idProblematica)
                    .mapTo(Int::class.java)
                    .findFirst()
        }
    }

    @Throws(UnableToExecuteStatementException::class)
    fun avanzarFaseProblematica(idProblematica: Int): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> { handle ->
            handle.createUpdate("UPDATE PROBLEMATICA SET c_fase = c_fase + 1 where c_id = :idProblematica")
                    .bind("idProblematica", idProblematica)
                    .execute() > 0
        }
    }
}