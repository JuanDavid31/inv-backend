package dao

import entity.Relacion
import org.jdbi.v3.core.Jdbi

class DaoRelacion(val jdbi: Jdbi) {

    fun conectarNodos(id: Int, idPadre: Int, fase: Int): Boolean =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("INSERT INTO RELACION(c_id_nodo, c_id_nodo_padre, c_fase) VALUES (:id, :idPadre, :fase) ")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .bind("fase", fase)
                .execute() > 0
        }

    fun eliminarConexionesPadreEHijo(id: Int, idPadre: Int): Boolean =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("DELETE FROM RELACION WHERE c_id_nodo = :id AND c_id_nodo_padre = :idPadre AND c_fase = 1 ")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .execute() > 0
        }

    fun darRelacionesEntreNodos(idProblematica: Int): List<Relacion> {
        return jdbi.withHandle<List<Relacion>, Exception> {
            it.createQuery("""
                
            """.trimIndent())
                    .bind
        }
    }

}