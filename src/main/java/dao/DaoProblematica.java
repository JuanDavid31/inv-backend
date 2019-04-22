package dao;

import entity.Problematica;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class DaoProblematica {

    final Jdbi jdbi;

    public DaoProblematica(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public Problematica agregarProblematicaPorPersona(String email, Problematica problematica) {
        return jdbi.inTransaction(handle ->{
                Problematica nuevaProblematica =
                        handle.createUpdate("INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion) VALUES(:nombre, :descripcion, now())")
                        .bindBean(problematica)
                        .executeAndReturnGeneratedKeys()
                        .mapToBean(Problematica.class)
                        .findOnly();
                handle.createUpdate("INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) " +
                        "VALUES(concat(:email, :id), :email, :id, :interventor)")
                        .bind("email", email)
                        .bind("interventor", true)
                        .bindBean(nuevaProblematica)
                        .execute();
            return nuevaProblematica;
        });
    }

    public List<Problematica> darProblematicasPorPersona(String email){
        return jdbi.withHandle(h -> h.createQuery("SELECT DISTINCT P.c_id, P.a_nombre, P.a_descripcion, P.f_fecha_creacion, PP.b_interventor " +
                "FROM PROBLEMATICA P, PERSONA_PROBLEMATICA PP WHERE PP.a_email = :email ORDER BY P.c_id;")
                .bind("email",  email)
                .mapToBean(Problematica.class)
                .list());
    }
}
