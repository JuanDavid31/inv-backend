package dao

import entity.Relacion
import org.jdbi.v3.core.Jdbi

class DaoRelacion(val jdbi: Jdbi) {

    fun agregarNodoANodo(id: Int?, idPadre: Int?, fase: Int): Boolean =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("INSERT INTO RELACION(c_id_nodo, c_id_nodo_padre, c_fase) VALUES (:id, :idPadre, :fase) ")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .bind("fase", fase)
                .execute() > 0
        }

    fun eliminarNodoANodo(id: Int, idPadre: Int, fase: Int): Boolean =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("DELETE FROM RELACION WHERE c_id_nodo = :id AND c_id_nodo_padre = :idPadre AND c_fase = :fase ")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .bind("fase", fase)
                .execute() > 0
        }

    fun agregarNodoAGrupo(relacion: Relacion) =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("INSERT INTO RELACION(c_id_nodo, c_id_grupo_padre, c_fase) values(:idNodo, :idGrupoPadre, 2)")
            .bindBean(relacion)
            .execute() > 0
        }

    fun eliminarNodoAGrupo(relacion: Relacion) =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("DELETE FROM RELACION WHERE c_id_nodo = :idNodo AND c_id_grupo_padre = :idGrupoPadre")
                .bindBean(relacion)
                .execute() > 0
        }

    fun agregarGrupoAGrupo(relacion: Relacion): Boolean =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("INSERT INTO RELACION(c_id_grupo, c_id_grupo_padre, c_fase) values(:idGrupo, :idGrupoPadre, :fase)")
            .bindBean(relacion)
            .execute() > 0
        }

    fun eliminarGrupoAGrupo(relacion: Relacion): Boolean =
        jdbi.withHandle<Boolean, Exception> {
            it.createUpdate("DELETE FROM RELACION WHERE c_id_grupo = :idGrupo AND c_id_grupo_padre = :idGrupoPadre")
            .bindBean(relacion)
            .execute() > 0
        }

}