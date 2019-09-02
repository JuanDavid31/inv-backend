package dao;

import entity.Invitacion;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import java.util.List;
import java.util.Map;

public class DaoInvitacion {

    private final Jdbi jdbi;

    public DaoInvitacion(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    public List<Invitacion> darPersonasInvitadas(String emailRemitente, int idProblematica){
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT P.d_nombre, I.a_email_remitente, I.a_email_destinatario, I.c_id_problematica, I.a_id, I.b_para_interventor, I.b_rechazada " +
                "FROM PERSONA P, INVITACION I " +
                "WHERE P.a_email = I.a_email_destinatario AND I.a_email_remitente = :emailRemitente AND I.c_id_problematica = :idProblematica AND " +
                "I.b_vigente = true")
                .bind("emailRemitente", emailRemitente)
                .bind("idProblematica", idProblematica)
                .mapToBean(Invitacion.class)
                .list());
    }

    /**
     *
     * @param invitacion con los atributos emailRemitente, emailDestinatario, idProblematica y paraInterventor
     * @return La nueva invitacion agregada
     */
    public Invitacion agregarInvitacion(Invitacion invitacion){
        return jdbi.withHandle(handle ->
                handle.createUpdate("INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada) " +
                "VALUES(:emailRemitente, :emailDestinatario, :idProblematica, concat(:idProblematica, :emailRemitente, :emailDestinatario), true, :paraInterventor, false)")
                .bindBean(invitacion)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Invitacion.class)
                .findOnly());
    }

    /**
     *
     * @param invitacion Con los atributos emailRemitente, emailDestinatario y idProblematica
     * @param idInvitacion
     * @return True si se elimino, False en caso contrario
     */
    public boolean eliminarInvitacion(Invitacion invitacion, String idInvitacion)throws UnableToExecuteStatementException {
        return jdbi.withHandle(handle ->
                handle.createUpdate("DELETE FROM INVITACION I WHERE I.a_email_destinatario = :emailDestinatario AND " +
                "I.a_email_remitente = :emailRemitente AND I.c_id_problematica = :idProblematica AND a_id = :idInvitacion")
                .bind("idInvitacion", idInvitacion)
                .bindBean(invitacion)
                .execute()) > 0;
    }

    /**
     * Devuelve las invitaciones recibidas que no han sido aceptadas pero que siguen vigentes.
     * @param emailDestinatario
     * @return List que contiene Map(s) con los atributos id_problematica, nombre_remitente, email_remitente, para_interventor,
     * nombre_problematica, descripcion_problematica y fecha_creacion_problematica.
     */
    public List<Map<String, Object>> darInvitacionesVigentesRecibidas(String emailDestinatario){
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT PRO.c_id as \"idProblematica\", P.d_nombre as \"nombreRemitente\", I.a_email_remitente as \"emailRemitente\", " +
                "I.b_para_interventor as \"paraInterventor\", PRO.a_nombre as \"nombreProblematica\", PRO.a_descripcion as \"descripcionProblematica\", " +
                "PRO.f_fecha_creacion as fecha_creacion_problematica " +
                "FROM PERSONA P, INVITACION I, PROBLEMATICA PRO " +
                "WHERE P.a_email = I.a_email_remitente AND I.a_email_destinatario = :emailDestinatario AND I.b_vigente = true AND " +
                "I.c_id_problematica = PRO.c_id AND I.b_rechazada = false")
                .bind("emailDestinatario", emailDestinatario)
                .mapToMap()
                .list());
    }

    /**
     * Transacción que crea un registro en PERSONA_PROBLEMATICA y actualiza otro en INVITACION
     * @param invitacion Con los atributos emailDestinatario, idProblematica, paraInterventor
     * @param idInvitacion
     * @return True si se acepto, False en caso contrario
     */
    public boolean aceptarInvitacion(Invitacion invitacion, String idInvitacion) throws UnableToExecuteStatementException {
        return jdbi.inTransaction(handle -> {
            boolean seAgregoPersonaProblematica = handle.createUpdate("INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) " +
                    "VALUES(concat(:emailDestinatario, :idProblematica), :emailDestinatario, :idProblematica, :paraInterventor)")
                    .bindBean(invitacion)
                    .execute() > 0;

            boolean seEliminoInvitacion = handle.createUpdate("UPDATE INVITACION SET b_vigente = false WHERE a_email_destinatario = :emailDestinatario AND " +
                    "c_id_problematica = :idProblematica AND a_id = :idInvitacion")
                    .bind("idInvitacion", idInvitacion)
                    .bindBean(invitacion)
                    .execute() > 0;

            if(!(seAgregoPersonaProblematica || seEliminoInvitacion)){
                handle.rollback();
                return false;
            }else{
                return true;
            }
        });
    }

    /**
     *
     * @param invitacion Con los atributos emailDestinatario, emailRemitente y idProblematica
     * @param idInvitacion
     * @return True si se rechazo, False en caso contrarior
     */
    public boolean rechazarInvitacion(Invitacion invitacion, String idInvitacion) throws UnableToExecuteStatementException {
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE INVITACION SET b_rechazada = true, b_vigente = false " +
            "WHERE a_email_destinatario = :emailDestinatario AND a_email_remitente = :emailRemitente AND c_id_problematica = :idProblematica")
            .bindBean(invitacion)
            .execute() > 0);
    }
}