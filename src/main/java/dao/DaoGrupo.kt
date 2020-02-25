package dao

import entity.Grupo
import entity.GrupoConReaccion
import org.jdbi.v3.core.Jdbi
import java.util.Optional

class DaoGrupo(private val jdbi: Jdbi) {

    fun darGrupos(idProblematica: Int): MutableList<Grupo> {
        return jdbi.withHandle<MutableList<Grupo>, RuntimeException> {
            it.createQuery("""SELECT G.c_id, G.d_nombre, G.c_id_problematica, R.c_id_grupo_padre as c_id_padre FROM GRUPO G
                INNER JOIN RELACION R ON R.c_id_grupo = G.c_id
|               WHERE G.c_id_problematica = :idProblematica
|               AND R.c_fase = 2""".trimMargin())
                .bind("idProblematica", idProblematica)
                .mapToBean(Grupo::class.java)
                .list()
        }
    }

    fun agregarGrupo(idProblematica: Int, grupo: Grupo): Grupo {
        return jdbi.withHandle<Grupo, RuntimeException> { handle ->
            handle.createUpdate("INSERT INTO GRUPO(c_id_problematica, d_nombre, c_id_padre) VALUES(:idProblematica, :nombre, :idPadre)")
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

    fun darGruposConReacciones(idProblematica: Int): List<GrupoConReaccion> {
        return jdbi.withHandle<List<GrupoConReaccion>, RuntimeException> {
            it.createQuery("""
                SELECT c_id, d_nombre, c_id_padre, V.negativa, V.neutra, V.positiva FROM GRUPO INNER JOIN
                VISTA_CONTEO_REACCIONES V on V.c_id_grupo = c_id
                WHERE c_id_problematica = :idProblematica
                """.trimIndent())
                .bind("idProblematica", idProblematica)
                .mapToBean(GrupoConReaccion::class.java)
                .list()
        }
    }

    fun darGrupoConReaccion(idProblematica: Int, idPersonaProblematica: String): Optional<Grupo> {
        return jdbi.withHandle<Optional<Grupo>, RuntimeException> {
            it.createQuery("SELECT G.c_id, G.D_NOMBRE, R.c_valor FROM GRUPO G, REACCION R " +
                "WHERE G.c_id = R.c_id_grupo AND G.c_id_problematica = :idProblematica AND R.a_id_pers_prob = :idPersonaProblematica")
                .bind("idProblematica", idProblematica)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .mapToBean(Grupo::class.java)
                .findFirst()
        }
    }

    fun eliminarGrupos(idsGrupos: List<Int>, idProblematica: Int): Boolean {
        return try {
            jdbi.withHandle<Boolean, Exception> {
                it.createUpdate("DELETE FROM GRUPO WHERE c_id in (<idsGrupos>) AND c_id_problematica = :idProblematica")
                    .bindList("idsGrupos", idsGrupos) //Si la lista esta vacia entonces se lanza una excepción
                    .bind("idProblematica", idProblematica)
                    .execute() > 0
            }
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun eliminarConexiones(idsGrupos: List<Int>): Boolean {
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