package dao

import entity.Escrito
import org.jdbi.v3.core.Jdbi
import java.lang.Exception
import java.util.*

class DaoEscrito(private val jdbi: Jdbi){

    fun darEscritosPorProblematica(idProblematica: Int): List<Escrito>{
        return jdbi.withHandle<List<Escrito>, Exception>{
            it.createQuery("SELECT P.C_ID, E.A_DESCRIPCION FROM PROBLEMATICA P, GRUPO G, ESCRITO E WHERE " +
                "P.C_ID = :idProblematica AND P.C_FASE = 5 AND P.C_ID = G.C_ID_PROBLEMATICA AND G.C_ID = E.C_ID_GRUPO")
                .bind("idProblematica", idProblematica)
                .mapToBean(Escrito::class.java)
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