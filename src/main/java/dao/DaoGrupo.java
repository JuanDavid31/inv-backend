package dao;

import entity.Grupo;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class DaoGrupo {
    
    private final Jdbi jdbi;
    
    public DaoGrupo(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public List<Grupo> darGrupos(int idProblematica){
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM GRUPO WHERE c_id_problematica = :idProblematica")
                .bind("idProblematica", idProblematica)
                .mapToBean(Grupo.class)
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

    public boolean actualizarGrupo(int idGrupo, Grupo grupo){
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE GRUPO SET d_nombre = :nombre where c_id = :idGrupo")
                .bind("idGrupo", idGrupo)
                .bindBean(grupo)
                .execute() > 0);
    }

    public boolean apadrinar(int id, int idPadre, int idProblematica){
        return jdbi.withHandle(handle ->handle.createUpdate("UPDATE GRUPO SET c_id_padre = :idPadre WHERE c_id = :id AND c_id_problematica = :idProblematica")
                .bind("id", id)
                .bind("idPadre", idPadre)
                .bind("idProblematica", idProblematica)
                .execute() > 0);
    }

    public boolean desApadrinar(int id, int idProblematica){
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

    public boolean eliminarGrupo(int id, int idProblematica) {
        return jdbi.inTransaction(handle -> {
            desApadrinar(id, idProblematica);

            return handle.createUpdate("DELETE FROM GRUPO WHERE c_id = :id AND c_id_problematica = :idProblematica")
                    .bind("id", id)
                    .bind("idProblematica", idProblematica)
                    .execute() > 0;
        });
    }
}
