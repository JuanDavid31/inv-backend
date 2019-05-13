package dao

import entity.Escrito
import org.jdbi.v3.core.Jdbi
import java.lang.Exception

class DaoEscrito(private val jdbi: Jdbi){

    fun darEscritosPorProblematica(idProblematica: Int): List<Escrito>{
        return jdbi.withHandle<List<Escrito>, Exception>{
            it.createQuery("SELECT P.C_ID, E.A_DESCRIPCION FROM PROBLEMATICA P, GRUPO G, ESCRITO E WHERE " +
                "P.C_ID = :idProblematica AND P.C_ID = G.C_ID_PROBLEMATICA AND G.C_ID = E.C_ID_GRUPO")
                .bind("idProblematica", idProblematica)
                .mapToBean(Escrito::class.java)
                .list()
        }
    }

    fun darEscritoPorPersona(idPersonaProblematica: String): Escrito{
        return jdbi.withHandle<Escrito, Exception>{
            it.createQuery("SELECT * FROM ESCRITO WHERE a_id_pers_prob = :idPersonaProblematica")
                .bind("idPersonaProblematica", idPersonaProblematica)
                .mapToBean(Escrito::class.java)
                .findOnly()
        }
    }

    fun agregarEscrito(escrito: Escrito, idPersonaProblematica: String): Escrito {
        return jdbi.withHandle<Escrito, Exception>{
            it.createUpdate("INSERT INTO ESCRITO(a_descripcion, c_id_grupo, a_id_pers_prob) VALUES(:descripcion, :idGrupo, :idPersProb)")
                .bindBean(escrito)
                .bind("idPersProb", idPersonaProblematica)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Escrito::class.java)
                .findOnly()
        }
    }

    fun editarEscrito(escrito: Escrito, idPersonaProblematica: String, idEscrito: String): Boolean{
        return jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("UPDATE ESCRITO SET d_descripcion = :nuevaDescripcion WHERE c_id = :idEscrito AND a_id_pers_prob = :idPersProb")
                .bindBean(escrito)
                .bind("idEscrito", idEscrito)
                .bind("idPersProb", idPersonaProblematica)
                .execute() > 0
        }
    }


}