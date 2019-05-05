package dao;

import org.jdbi.v3.core.Jdbi;

public class DaoReaccion {

    private final Jdbi jdbi;

    public DaoReaccion(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public boolean reaccionar(int valor, int idGrupo, String idPersonaProblematica){
        return jdbi.withHandle(handle ->
                handle.createUpdate("INSERT INTO REACCION(c_valor, c_id_grupo, a_id_pers_prob) VALUES(:valor, :idGrupo, :idPersonaProblematica)")
                .bind("valor", valor)
                .bind("idGrupo", idGrupo)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .execute() > 0);
    }
}
