package dao

import entity.Grupo
import org.jdbi.v3.core.Jdbi
import java.util.Optional

class DaoGrupo(private val jdbi: Jdbi) {

    fun darGrupos(idProblematica: Long): MutableList<Grupo> {
        return jdbi.withHandle<MutableList<Grupo>, RuntimeException> {
            it.createQuery("SELECT * FROM GRUPO G WHERE G.c_id_problematica = :idProblematica")
                .bind("idProblematica", idProblematica)
                .mapToBean(Grupo::class.java)
                .list()
        }
    }

    fun agregarGrupo(idProblematica: Int, grupo: Grupo): Grupo {
        return jdbi.withHandle<Grupo, RuntimeException> { handle ->
            handle.createUpdate("INSERT INTO GRUPO(c_id_problematica, d_nombre) VALUES(:idProblematica, :nombre)")
                .bind("idProblematica", idProblematica)
                .bindBean(grupo)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Grupo::class.java)
                .findOnly()
        }
    }

    fun actualizarNombreYPadreGrupo(grupo: Grupo): Boolean {
        return try{
            jdbi.withHandle<Boolean, RuntimeException> {
                it.createUpdate("UPDATE GRUPO SET d_nombre = :nombre, c_id_padre = :idPadre WHERE c_id = :id")
                    .bindBean(grupo)
                    .execute() > 0
            }
        }catch(e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun apadrinar(id: Int, idPadre: Int, idProblematica: Int): Boolean {
        return try{
            jdbi.withHandle<Boolean, RuntimeException> {
                it.createUpdate("UPDATE GRUPO SET c_id_padre = :idPadre WHERE c_id = :id AND c_id_problematica = :idProblematica")
                        .bind("id", id)
                        .bind("idPadre", idPadre)
                        .bind("idProblematica", idProblematica)
                        .execute() > 0
            }
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun desApadrinar(id: Int, idProblematica: Int): Boolean {
        return try{
            jdbi.withHandle<Boolean, RuntimeException> {
                it.createUpdate("UPDATE GRUPO SET c_id_padre = null WHERE c_id = :id AND c_id_problematica = :idProblematica")
                        .bind("id", id)
                        .bind("idProblematica", idProblematica)
                        .execute() > 0
            }
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun darGruposConReacciones(idProblematica: Int): List<Grupo> {
        return jdbi.withHandle<List<Grupo>, RuntimeException> { handle ->
            handle.createQuery("SELECT D_NOMBRE, R.c_valor, COUNT(R.C_VALOR) FROM GRUPO G, REACCION R WHERE G.c_id = R.c_id_grupo AND " + "G.c_id_problematica = :idProblematica GROUP BY c_valor, G.c_id ORDER BY count desc")
                .bind("idProblematica", idProblematica)
                .mapToBean(Grupo::class.java)
                .list()
        }
    }

    fun darGrupoConReaccion(idProblematica: Int, idPersonaProblematica: String): Optional<Grupo> {
        return jdbi.withHandle<Optional<Grupo>, RuntimeException> { handle ->
            handle.createQuery("SELECT G.c_id, G.D_NOMBRE, R.c_valor FROM GRUPO G, REACCION R " + "WHERE G.c_id = R.c_id_grupo AND G.c_id_problematica = :idProblematica AND R.a_id_pers_prob = :idPersonaProblematica")
                .bind("idProblematica", idProblematica)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .mapToBean(Grupo::class.java)
                .findFirst()
        }
    }

    fun eliminarGrupo(id: Int, idProblematica: Int): Boolean {
        return try{
            jdbi.inTransaction<Boolean, RuntimeException> {
                desApadrinar(id, idProblematica)

                it.createUpdate("UPDATE NODO SET c_id_padre = null WHERE c_id_padre = :idPadre")
                        .bind("idPadre", id)
                        .execute()

                it.createUpdate("DELETE FROM GRUPO WHERE c_id = :id AND c_id_problematica = :idProblematica")
                        .bind("id", id)
                        .bind("idProblematica", idProblematica)
                        .execute() > 0
            }
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun eliminarGrupos(idsGrupos: List<Int>, idProblematica: Int): Boolean {
        return try {
            jdbi.withHandle<Boolean, Exception> {
                it.createUpdate("DELETE FROM GRUPO WHERE c_id in (<idsGrupos>) AND c_id_problematica = :idProblematica")
                    .bindList("idsGrupos", idsGrupos) //TODO: Si la lista esta vacia entonces se lanza una excepción
                    .bind("idProblematica", idProblematica)
                    .execute() > 0
            }
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun eliminarConexiones(idsGrupos: Any): Boolean {
        return try {
            jdbi.withHandle<Boolean, Exception> {
                it.createUpdate("UPDATE GRUPO SET c_id_padre = null WHERE c_id in (<idsGrupos>)")
                        .bindList("idsGrupos", idsGrupos)
                        .execute() > 0
            }
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}