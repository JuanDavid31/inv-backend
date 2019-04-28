package dao;

import entity.Nodo;
import org.jdbi.v3.core.Jdbi;

public class DaoNodo {

    private final Jdbi jdbi;

    public DaoNodo(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public int agregarNodo(Nodo nodo){
        return jdbi.inTransaction(handle ->handle.createUpdate("INSERT INTO NODO(a_id_pers_prob) VALUES(concat(:email, :idProblematica))")
                    .bindBean(nodo)
                    .executeAndReturnGeneratedKeys()
                    .mapTo(Integer.class)
                    .findOnly());
    }

    public boolean actualizarNodo(Nodo nodo){
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE NODO SET a_url_foto = :urlFoto where c_id = :id")
                .bindBean(nodo)
                .execute() > 0);
    }

    public Nodo eliminarNodo(int id) {
        return jdbi.withHandle(handle -> handle.createUpdate("DELETE FROM NODO WHERE c_id = :id")
                .bind("id", id)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Nodo.class)
                .findOnly());
    }
}
