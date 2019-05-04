package dao;

import entity.Invitacion;
import org.jdbi.v3.core.Jdbi;

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

    public Invitacion agregarInvitacion(Invitacion invitacion){
        return jdbi.withHandle(handle ->
                handle.createUpdate("INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, b_para_interventor, b_rechazada) " +
                "VALUES(:emailRemitente, :emailDestinatario, :idProblematica, concat(:idProblematica, :emailRemitente, :emailDestinatario), true, :paraInterventor, false)")
                .bindBean(invitacion)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Invitacion.class)
                .findOnly());
    }

    public boolean eliminarInvitacion(Invitacion invitacion, String idInvitacion){
        return jdbi.withHandle(handle ->
                handle.createUpdate("DELETE FROM INVITACION I WHERE I.a_email_destinatario = :emailDestinatario AND " +
                "I.a_email_remitente = :emailRemitente AND I.c_id_problematica = :idProblematica AND a_id = :idInvitacion")
                .bind("idInvitacion", idInvitacion)
                .bindBean(invitacion)
                .execute()) > 0;
    }

    public List<Map<String, Object>> darInvitacionesVigentes(String emailDestinatario){
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT PRO.c_id as id_problematica, P.d_nombre as nombre_remitente, I.a_email_remitente as email_remitente, I.b_para_interventor " +
                "as para_interventor, PRO.a_nombre as nombre_problematica, PRO.a_descripcion as descripcion_problematica, PRO.f_fecha_creacion as fecha_creacion_problematica " +
                "FROM PERSONA P, INVITACION I, PROBLEMATICA PRO " +
                "WHERE P.a_email = I.a_email_remitente AND I.a_email_destinatario = :emailDestinatario AND I.b_vigente = true AND I.c_id_problematica = PRO.c_id AND " +
                "I.b_rechazada = false")
                .bind("emailDestinatario", emailDestinatario)
                .mapToMap()
                .list());
    }

    public boolean aceptarInvitacion(Invitacion invitacion, String idInvitacion){
        return jdbi.inTransaction(handle -> {
            boolean seAgrego = handle.createUpdate("INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) " +
                    "VALUES(concat(:emailDestinatario, :idProblematica), :emailDestinatario, :idProblematica, :paraInterventor)")
                    .bindBean(invitacion)
                    .execute() > 0;

            boolean seElimino = handle.createUpdate("UPDATE INVITACION SET b_vigente = false WHERE a_email_destinatario = :emailDestinatario AND " +
                    "c_id_problematica = :idProblematica AND a_id = :idInvitacion")
                    .bind("idInvitacion", idInvitacion)
                    .bindBean(invitacion)
                    .execute() > 0;

            if(!(seAgrego && seElimino))handle.rollback();
            return true;
        });
    }

    public boolean rechazarInvitacion(Invitacion invitacion, String idInvitacion){
        return jdbi.withHandle(handle -> handle.createUpdate("UPDATE INVITACION SET b_rechazada = true, b_vigente = false " +
            "WHERE a_email_destinatario = :emailDestinatario AND a_email_remitente = :emailRemitente AND c_id_problematica = :idProblematica")
            .bindBean(invitacion)
            .execute() > 0);
    }
}