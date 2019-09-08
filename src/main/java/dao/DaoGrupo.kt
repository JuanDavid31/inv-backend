package dao

import entity.Grupo
import org.jdbi.v3.core.Jdbi
import java.util.Optional

class DaoGrupo(private val jdbi: Jdbi) {

    fun darGrupos(idProblematica: Int): List<Map<String, Any>> {
        return jdbi.withHandle<List<Map<String, Any>>, RuntimeException> { handle ->
            handle.createQuery("SELECT g.c_id as  \"idGrupo\", g.c_id_padre as \"idPadreGrupo\", g.d_nombre as \"nombreGrupo\", " +
                "n.a_url_foto as \"urlFotoNodo\", n.c_id_padre as \"idPadreNodo\" " +
                "from problematica p, grupo g, nodo n " +
                "where p.c_id = g.c_id_problematica and p.c_id = :idProblematica and g.c_id = n.c_id_grupo")
                .bind("idProblematica", idProblematica)
                .mapToMap()
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

    fun actualizarGrupo(idGrupo: Int, grupo: Grupo): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> { handle ->
            handle.createUpdate("UPDATE GRUPO SET d_nombre = :nombre where c_id = :idGrupo")
                .bind("idGrupo", idGrupo)
                .bindBean(grupo)
                .execute() > 0
        }
    }

    fun apadrinar(id: Int, idPadre: Int, idProblematica: Int): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> { handle ->
            handle.createUpdate("UPDATE GRUPO SET c_id_padre = :idPadre WHERE c_id = :id AND c_id_problematica = :idProblematica")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .bind("idProblematica", idProblematica)
                .execute() > 0
        }
    }

    fun desApadrinar(id: Int, idProblematica: Int): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> { handle ->
            handle.createUpdate("UPDATE GRUPO SET c_id_padre = null WHERE c_id = :id AND c_id_problematica = :idProblematica")
                .bind("id", id)
                .bind("idProblematica", idProblematica)
                .execute() > 0
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
        return jdbi.inTransaction<Boolean, RuntimeException> { handle ->
            desApadrinar(id, idProblematica)

            handle.createUpdate("DELETE FROM GRUPO WHERE c_id = :id AND c_id_problematica = :idProblematica")
                .bind("id", id)
                .bind("idProblematica", idProblematica)
                .execute() > 0
        }
    }
}