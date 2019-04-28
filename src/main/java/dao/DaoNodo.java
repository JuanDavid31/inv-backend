package dao;

import entity.Nodo;
import org.jdbi.v3.core.Jdbi;

public class DaoNodo {

    private final Jdbi jdbi;

    public DaoNodo(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public boolean agregarNodo(Nodo nodo){
        return jdbi.withHandle(handle ->
                handle.createUpdate("INSERT INTO NODO(a_email, c_id_problematica, a_url_foto) VALUES(:email, :idProblematica, :urlFoto)")
                .bindBean(nodo)
                .execute() > 0);
    }
}
