package dao

import entity.Nodo
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.lang.Exception

/**
 * Manejo de excepciones completo
 */
class DaoNodo(private val jdbi: Jdbi) {

    fun darNodos(idPersonaProblematica: String): List<Nodo> {
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

    fun actualizarNodo(nodo: Nodo): Boolean {
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
     * Dado el id del nodo padre, se buscara todo nodo hijo y se eliminara la relación entre ellos.
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
        return jdbi.withHandle<Nodo, RuntimeException> {
            try{
                val idProblematica = it.createQuery(""" SELECT PERSONA_PROBLEMATICA.c_id_problematica FROM NODO, PERSONA_PROBLEMATICA
                        WHERE a_id_pers_prob = PERSONA_PROBLEMATICA.a_id and NODO.c_id = :id""")
                        .bind("id", id)
                        .mapTo(Int::class.java)
                        .findOnly()

                val nodo = it.createUpdate("DELETE FROM NODO WHERE c_id = :id")
                    .bind("id", id)
                    .executeAndReturnGeneratedKeys()
                    .mapToBean(Nodo::class.java)
                    .findOnly()
                nodo.idProblematica = idProblematica
                nodo
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }
}