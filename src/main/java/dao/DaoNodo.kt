package dao

import entity.Nodo
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.lang.Exception

/**
 * Manejo de excepciones completo
 */
class DaoNodo(private val jdbi: Jdbi) {

    fun darNodosPorPersonaYProblematica(idPersonaProblematica: String): List<Nodo> {
        return jdbi.withHandle<List<Nodo>, Exception> {
            try {
                it.createQuery("SELECT * FROM NODO WHERE a_id_pers_prob = :idPersProb")
                    .bind("idPersProb", idPersonaProblematica)
                    .mapToBean(Nodo::class.java)
                    .list()
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Consulta los nodos que esten o no asociados a un grupo junto con información adicional.
     */
    fun darNodosPorProblematica(idProblematica: Int): List<Nodo>{
        return jdbi.withHandle<List<Nodo>, Exception> {
            try {
                it.createQuery("""SELECT N.c_id, N.a_nombre, N.a_url_foto, N.c_id_grupo, concat(p.d_nombres, ' ', p.d_apellidos) as "nombreCreador"
                FROM NODO n LEFT JOIN GRUPO G on G.c_id = N.c_id_grupo
                inner join PERSONA_PROBLEMATICA pp on pp.a_id = n.a_id_pers_prob
                inner join persona p on pp.a_email = p.a_email
                where PP.c_id_problematica = :idProblematica""")
                .bind("idProblematica", idProblematica)
                .mapToBean(Nodo::class.java)
                .list()
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    fun agregarNodo(nodo: Nodo): Int {
        return jdbi.withHandle<Int, Exception> {
            try {
                it.createUpdate("INSERT INTO NODO(a_id_pers_prob, a_nombre) VALUES(concat(:email, :idProblematica), :nombre)")
                    .bindBean(nodo)
                    .executeAndReturnGeneratedKeys()
                    .mapTo(Int::class.java)
                    .findOnly()
            }catch (e: Exception){
                e.printStackTrace()
                0
            }

        }
    }

    fun actualizarUrlNodo(nodo: Nodo): Boolean {
        return jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("UPDATE NODO SET a_url_foto = :urlFoto where c_id = :id")
                .bindBean(nodo)
                .execute() > 0
        }
    }

    fun apadrinar(id: Int, idPadre: Int): Boolean {
        return jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("UPDATE NODO SET c_id_padre = :idPadre WHERE c_id = :id")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .execute() > 0
        }
    }

    /**
     * Dado el id del nodo padre, se buscaran nodos hijo y se eliminara la relación entre ellos.
     * Los nodos hijos perderan a su padre.
     * @param idPadre
     * @return
     * @throws UnableToExecuteStatementException
     */
    fun eliminarConexionesPadreEHijo(idNodo: Int): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> {
            it.createUpdate("UPDATE NODO SET c_id_padre = null WHERE c_id = :id")
                .bind("id", idNodo)
                .execute() > 0
        }
    }

    fun eliminarNodo(id: Int): Nodo {
        return jdbi.inTransaction<Nodo, RuntimeException> {
            try{
                val idProblematica = darIdProblematica(it, id)
                eliminarConexionConHijo(it, id)
                val nodo = eliminarElementoNodo(it, id)
                nodo.idProblematica = idProblematica
                nodo
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    private fun darIdProblematica(handle: Handle, idNodo: Int) =
        handle.createQuery(""" SELECT PERSONA_PROBLEMATICA.c_id_problematica FROM NODO, PERSONA_PROBLEMATICA
            WHERE a_id_pers_prob = PERSONA_PROBLEMATICA.a_id and NODO.c_id = :idNodo""")
            .bind("idNodo", idNodo)
            .mapTo(Int::class.java)
            .findOnly()

    private fun eliminarConexionConHijo(handle: Handle, idNodo: Int){
        handle.createUpdate("UPDATE NODO SET c_id_padre = null WHERE c_id_padre = :id")
            .bind("id", idNodo)
            .execute()
    }

    private fun eliminarElementoNodo(handle: Handle, idNodo: Int) =
        handle.createUpdate("DELETE FROM NODO WHERE c_id = :id")
            .bind("id", idNodo)
            .executeAndReturnGeneratedKeys()
            .mapToBean(Nodo::class.java)
            .findOnly()

    fun actualizarGrupoNodo(nodo: Nodo): Boolean {
        return jdbi.withHandle<Boolean, Exception>{
            try{
                it.createUpdate("UPDATE NODO SET c_id_grupo = :idGrupo WHERE c_id = :id")
                .bindBean(nodo)
                .execute() > 0
            }catch (e: Exception){
                e.printStackTrace()
                false
            }
        }
    }

}