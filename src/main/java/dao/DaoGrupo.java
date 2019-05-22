package dao;

import entity.Grupo;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DaoGrupo {
    
    private final Jdbi jdbi;
    
    public DaoGrupo(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public List<Map<String, Object>> darGrupos(int idProblematica){
        return jdbi.withHandle(handle ->
                handle.createQuery("select g.c_id as id_grupo, g.c_id_padre as id_padre_grupo, g.d_nombre as nombre_grupo, n.a_url_foto url_foto, n.c_id_padre id_padre_nodo" +
                "from problematica p, grupo g, nodo n" +
                "where p.c_id = g.c_id_problematica and p.c_id = :idProblematica and g.c_id = n.c_id_grupo")
                .bind("idProblematica", idProblematica)
                .mapToMap()
                .list());
    }

    public Grupo agregarGrupo(int idProblematica, Grupo grupo){
        return jdbi.withHandle(handle ->handle.createUpdate("INSERT INTO GRUPO(c_id_problematica, d_nombre) VALUES(:idProblematica, :nombre)")
                .bind("idProblematica", idProblematica)
                .bindBean(grupo)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Grupo.class)
                .findOnly());
    }

    public boolean actualizarGrupo(int idGrupo, Grupo grupo) throws UnableToExecuteStatementException{
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE GRUPO SET d_nombre = :nombre where c_id = :idGrupo")
                .bind("idGrupo", idGrupo)
                .bindBean(grupo)
                .execute() > 0);
    }

    public boolean apadrinar(int id, int idPadre, int idProblematica) throws UnableToExecuteStatementException{
        return jdbi.withHandle(handle ->handle.createUpdate("UPDATE GRUPO SET c_id_padre = :idPadre WHERE c_id = :id AND c_id_problematica = :idProblematica")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .bind("idProblematica", idProblematica)
                .execute() > 0);
    }

    public boolean desApadrinar(int id, int idProblematica) throws UnableToExecuteStatementException {
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE GRUPO SET c_id_padre = null WHERE c_id = :id AND c_id_problematica = :idProblematica")
                .bind("id", id)
                .bind("idProblematica", idProblematica)
                .execute() > 0);
    }

    public List<Grupo> darGruposConReacciones(int idProblematica){
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT D_NOMBRE, R.c_valor, COUNT(R.C_VALOR) FROM GRUPO G, REACCION R WHERE G.c_id = R.c_id_grupo AND " +
                "G.c_id_problematica = :idProblematica GROUP BY c_valor, G.c_id ORDER BY count desc")
                .bind("idProblematica", idProblematica)
                .mapToBean(Grupo.class)
                .list());
    }

    public Optional<Grupo> darGrupoConReaccion(int idProblematica, String idPersonaProblematica){
        return jdbi.withHandle(handle -> handle.createQuery("SELECT G.c_id, G.D_NOMBRE, R.c_valor FROM GRUPO G, REACCION R " +
                "WHERE G.c_id = R.c_id_grupo AND G.c_id_problematica = :idProblematica AND R.a_id_pers_prob = :idPersonaProblematica")
                .bind("idProblematica", idProblematica)
                .bind("idPersonaProblematica", idPersonaProblematica)
                .mapToBean(Grupo.class)
                .findFirst());
    }

    public boolean eliminarGrupo(int id, int idProblematica) throws UnableToExecuteStatementException {
        return jdbi.inTransaction(handle -> {
            desApadrinar(id, idProblematica);

            return handle.createUpdate("DELETE FROM GRUPO WHERE c_id = :id AND c_id_problematica = :idProblematica")
                    .bind("id", id)
                    .bind("idProblematica", idProblematica)
                    .execute() > 0;
        });
    }
}
