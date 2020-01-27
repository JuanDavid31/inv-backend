package dao

import entity.Escrito
import org.jdbi.v3.core.Jdbi
import java.lang.Exception
import java.util.*

class DaoEscrito(private val jdbi: Jdbi){

    fun darEscritos(idProblematica: Int): List<MutableMap<String, Any>>{
        return jdbi.withHandle<List<MutableMap<String, Any>>, Exception>{
            it.createQuery("""
                SELECT
                    G.d_nombre "nombreGrupo", E.a_nombre "nombre", E.a_descripcion "descripcion", 
                    p.a_email as "emailAutor", concat(p.d_nombres, ' ', p.d_apellidos) "autor"
                FROM 
                    PERSONA P
                INNER JOIN persona_problematica PP ON P.a_email = PP.a_email
                INNER JOIN ESCRITO E ON PP.a_id = E.a_id_pers_prob
                INNER JOIN GRUPO G ON E.c_id_grupo = G.c_id
                WHERE PP.c_id_problematica = :idProblematica
                """.trimIndent())
                    .bind("idProblematica", idProblematica)
                    .mapToMap()
                    .list()
        }
    }

    fun darEscritos(idPersonaProblematica: String): List<Escrito>{
        return jdbi.withHandle<List<Escrito>, Exception>{
            it.createQuery("SELECT * FROM ESCRITO WHERE a_id_pers_prob = :idPersonaProblematica")
                .bind("idPersonaProblematica", idPersonaProblematica)
                .mapToBean(Escrito::class.java)
                .list()
        }
    }

    fun agregarEscrito(escrito: Escrito, idPersonaProblematica: String): Escrito?{
        return jdbi.withHandle<Escrito?, Exception>{
            try{
                it.createUpdate("""INSERT INTO ESCRITO(a_nombre, a_descripcion, c_id_grupo, a_id_pers_prob) 
|               VALUES(:nombre, :descripcion, :idGrupo, :idPersProb)""".trimMargin())
                    .bindBean(escrito)
                    .bind("idPersProb", idPersonaProblematica)
                    .executeAndReturnGeneratedKeys()
                    .mapToBean(Escrito::class.java)
                    .findOnly()
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    fun editarEscrito(escrito: Escrito, idPersonaProblematica: String, idEscrito: Int): Escrito?{
        return jdbi.withHandle<Escrito, Exception> {
            try {
                it.createUpdate("UPDATE ESCRITO SET a_nombre = :nombre, a_descripcion = :descripcion " +
                    "WHERE c_id = :idEscrito AND a_id_pers_prob = :idPersProb")
                    .bindBean(escrito)
                    .bind("idEscrito", idEscrito)
                    .bind("idPersProb", idPersonaProblematica)
                    .executeAndReturnGeneratedKeys()
                    .mapToBean(Escrito::class.java)
                    .findOnly()
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    fun darEscrito(idPersonaProblematica: String, idGrupo: Int): Optional<Escrito> {
        return jdbi.withHandle<Optional<Escrito>, Exception> {
            it.createQuery("SELECT * FROM ESCRITO WHERE a_id_pers_prob = :idPersonaProblematica AND c_id_grupo = :idGrupo")
                .bind("idPersonaProblematica", idPersonaProblematica)
                .bind("idGrupo", idGrupo)
                .mapToBean(Escrito::class.java)
                .findFirst()
        }
    }

    fun eliminarEscrito(idPersonaProblematica: String, idEscrito: Int): Boolean {
        return jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("DELETE FROM ESCRITO WHERE a_id_pers_prob = :idPersonaProblematica AND c_id = :idEscrito")
                .bind("idPersonaProblematica", idPersonaProblematica)
                .bind("idEscrito", idEscrito)
                .execute() > 0
        }
    }


}