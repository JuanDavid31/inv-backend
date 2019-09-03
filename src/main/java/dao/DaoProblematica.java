package dao;

import entity.Problematica;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import java.util.List;
import java.util.Optional;

public class DaoProblematica {

    final Jdbi jdbi;

    public DaoProblematica(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public Problematica agregarProblematicaPorPersona(String email, Problematica problematica) {
        return jdbi.inTransaction(handle ->{
                Problematica nuevaProblematica = handle
                        .createUpdate("INSERT INTO PROBLEMATICA(a_nombre, a_descripcion, f_fecha_creacion, c_fase) VALUES(:nombre, :descripcion, now(), 0)")
                        .bindBean(problematica)
                        .executeAndReturnGeneratedKeys()
                        .mapToBean(Problematica.class)
                        .findOnly();
                boolean seAgrego =handle.createUpdate("INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) " +
                        "VALUES(concat(:email, :id), :email, :id, :interventor)")
                        .bind("email", email)
                        .bind("interventor", true)
                        .bindBean(nuevaProblematica)
                        .execute() > 0;
                if (!seAgrego) handle.rollback();
            return nuevaProblematica;
        });
    }

    public List<Problematica> darProblematicasPorPersona(String email){
        return jdbi.withHandle(h -> h.createQuery("SELECT DISTINCT P.c_id, P.a_nombre, P.a_descripcion, P.f_fecha_creacion, PP.b_interventor , PP.a_email " +
                "FROM PROBLEMATICA P, PERSONA_PROBLEMATICA PP, PERSONA " +
                "WHERE PERSONA.a_email = :email AND PERSONA.a_email = PP.a_email AND P.c_id = PP.c_id_problematica " +
                "ORDER BY P.c_id")
                .bind("email",  email)
                .mapToBean(Problematica.class)
                .list());
    }

    public Optional<Integer> darFase(int idProblematica){
        return jdbi.withHandle(handle -> handle.createQuery("select c_fase from problematica where c_id = :idProblematica")
                .bind("idProblematica", idProblematica)
                .mapTo(Integer.class)
                .findFirst());
    }

    public boolean avanzarFaseProblematica(int idProblematica) throws UnableToExecuteStatementException {
        return jdbi.withHandle(handle -> handle.createUpdate("update problematica set c_fase = c_fase + 1 where c_id = :idProblematica")
                .bind("idProblematica", idProblematica).execute() > 0);
    }
}