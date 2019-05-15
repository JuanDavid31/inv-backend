package dao;

import entity.Nodo;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class DaoNodo {

    private final Jdbi jdbi;

    public DaoNodo(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public List<Nodo> darNodos(String idPersonaProblematica){
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM NODO WHERE a_id_pers_prob = :idPersProb")
                .bind("idPersProb", idPersonaProblematica)
                .mapToBean(Nodo.class)
                .list());
    }

    public int agregarNodo(Nodo nodo){
        return jdbi.withHandle(handle ->handle.createUpdate("INSERT INTO NODO(a_id_pers_prob) VALUES(concat(:email, :idProblematica))")
                    .bindBean(nodo)
                    .executeAndReturnGeneratedKeys()
                    .mapTo(Integer.class)
                    .findOnly());
    }

    public boolean actualizarNodo(Nodo nodo){
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE NODO SET a_url_foto = :urlFoto, a_ruta_foto = :rutaFoto where c_id = :id")
                .bindBean(nodo)
                .execute() > 0);
    }

    public boolean apadrinar(int id, int idPadre){
        return jdbi.withHandle(handle ->handle.createUpdate("UPDATE NODO SET c_id_padre = :idPadre WHERE c_id = :id")
                    .bind("id", id)
                    .bind("idPadre", idPadre)
                    .execute() > 0);
    }

    public boolean desApadrinar(int id){
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE NODO SET c_id_padre = null WHERE c_id = :id")
                .bind("id", id)
                .execute() > 0);
    }

    public Nodo eliminarNodo(int id) {
        return jdbi.inTransaction(handle -> {
            desApadrinar(id);

            return handle.createUpdate("DELETE FROM NODO WHERE c_id = :id")
                    .bind("id", id)
                    .executeAndReturnGeneratedKeys()
                    .mapToBean(Nodo.class)
                    .findOnly();
        });
    }
}
