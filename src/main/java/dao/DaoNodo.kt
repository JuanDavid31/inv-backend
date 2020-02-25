package dao

import entity.Nodo
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.lang.Exception


class NodoIndividual{
    @get:ColumnName("c_id")
    var id: Int = 0
    @get:ColumnName("a_nombre")
    var nombre: String = ""
    @get:ColumnName("a_url_foto")
    var urlFoto: String = ""
    @get:ColumnName("c_id_nodo_padre")
    var idPadre: Int = 0
    constructor()
}

class ConexionNodo{
    @get:ColumnName("c_id")
    var id: Int = 0
    @get:ColumnName("c_id_nodo_padre")
    var idPadre: Int? = null
    constructor()
}


/**
 * Manejo de excepciones completo
 */
class DaoNodo(private val jdbi: Jdbi) {

    fun darNodosPorPersonaYProblematica(idPersonaProblematica: String): List<Nodo> {
        return jdbi.withHandle<List<Nodo>, Exception> {
            try {
                it.createQuery("""SELECT c_id, a_nombre, a_url_foto, c_id_nodo_padre FROM NODO N
                    INNER JOIN RELACION R ON R.c_id_nodo = N.c_id WHERE R.c_fase = 1 
                    AND a_id_pers_prob = :idPersProb""")
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
                it.createQuery("""SELECT N.c_id, N.a_nombre, N.a_url_foto, R.c_id_grupo_padre as c_id_grupo, 
                concat(p.d_nombres, ' ', p.d_apellidos) as "nombreCreador" FROM NODO n LEFT JOIN GRUPO G on G.c_id = N.c_id_grupo
                inner join PERSONA_PROBLEMATICA pp on pp.a_id = n.a_id_pers_prob
                inner join persona p on pp.a_email = p.a_email
                inner join RELACON R ON N.c_id = R.c_id_nodo
                where PP.c_id_problematica = :idProblematica AND R.c_fase = 2""")
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

    fun eliminarNodo(id: Int): Nodo {
        return jdbi.inTransaction<Nodo, RuntimeException> {
            try{
                val idProblematica = darIdProblematica(it, id)
                eliminarTodaConexion(it, id)
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

    private fun eliminarTodaConexion(handle: Handle, idNodo: Int){
        handle.createUpdate("DELETE FROM RELACION WHERE c_id_nodo = :id OR c_id_nodo_padre = :id")
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

    fun darConexionesSegundaFase(idProblematica: Int): List<ConexionNodo> =
        jdbi.withHandle<List<ConexionNodo>, Exception> {
            it.createQuery("""SELECT N.c_id, R.c_id_nodo_padre FROM NODO n
                inner join PERSONA_PROBLEMATICA pp on pp.a_id = n.a_id_pers_prob
                inner join RELACION R ON N.c_id = R.c_id_nodo
                where PP.c_id_problematica = :idProblematica AND R.c_fase = 2""")
                .bind("idProblematica", idProblematica)
                .mapToBean(ConexionNodo::class.java)
                .list()
        }



}