package dao;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

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

    public boolean eliminarReaccion(int idGrupo, String idPersonaProblematica) throws UnableToExecuteStatementException {
        return jdbi.withHandle(handle ->
                handle.createUpdate("DELETE FROM REACCION WHERE c_id_grupo = :idGrupo AND a_id_pers_prob = :idPersonaProblematica")
                .bind("idGrupo", idGrupo)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .execute() > 0);
    }
}
