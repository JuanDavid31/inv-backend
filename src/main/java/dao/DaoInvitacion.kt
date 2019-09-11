package dao

import entity.Invitacion
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

/**
 * Manejo de excepciones añadido.
 */
class DaoInvitacion(private val jdbi: Jdbi) {

    fun darPersonasInvitadas(emailRemitente: String, idProblematica: Int): List<Invitacion> {
        return jdbi.withHandle<List<Invitacion>, RuntimeException> { handle ->
            handle.createQuery("SELECT P.d_nombres, I.a_email_remitente, I.a_email_destinatario, I.c_id_problematica, I.a_id, I.b_para_interventor, I.b_rechazada " +
                    "FROM PERSONA P, INVITACION I " +
                    "WHERE P.a_email = I.a_email_destinatario AND I.a_email_remitente = :emailRemitente AND I.c_id_problematica = :idProblematica AND " +
                    "I.b_vigente = true")
                    .bind("emailRemitente", emailRemitente)
                    .bind("idProblematica", idProblematica)
                    .mapToBean(Invitacion::class.java)
                    .list()
        }
    }

    /**
     *
     * @param invitacion con los atributos emailRemitente, emailDestinatario, idProblematica y paraInterventor
     * @return La nueva invitacion agregada
     */
    fun agregarInvitacion(invitacion: Invitacion): Invitacion {
        return jdbi.withHandle<Invitacion, RuntimeException> { handle ->
            try {
                handle.createUpdate("INSERT INTO INVITACION(a_email_remitente, a_email_destinatario, c_id_problematica, a_id, b_vigente, " +
                        "b_para_interventor, b_rechazada) VALUES(:emailRemitente, :emailDestinatario, :idProblematica, concat(:idProblematica, :emailRemitente, " +
                        ":emailDestinatario), true, :paraInterventor, false)")
                        .bindBean(invitacion)
                        .executeAndReturnGeneratedKeys()
                        .mapToBean(Invitacion::class.java)
                        .findOnly()
            } catch (e: UnableToExecuteStatementException) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     *
     * @param invitacion Con los atributos emailRemitente, emailDestinatario y idProblematica
     * @param idInvitacion
     * @return True si se elimino, False en caso contrario
     */
    @Throws(UnableToExecuteStatementException::class)
    fun eliminarInvitacion(idInvitacion: String): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> { handle ->
            handle.createUpdate("DELETE FROM INVITACION I WHERE a_id = :idInvitacion")
                .bind("idInvitacion", idInvitacion)
                .execute() > 0
        }
    }

    /**
     * Devuelve las invitaciones recibidas que no han sido aceptadas pero que siguen vigentes.
     * @param emailDestinatario
     * @return List que contiene Map(s) con los atributos id_problematica, nombre_remitente, email_remitente, para_interventor,
     * nombre_problematica, descripcion_problematica y fecha_creacion_problematica.
     */
    fun darInvitacionesVigentesRecibidas(emailDestinatario: String): List<Any> {
        return jdbi.withHandle<List<Any>, RuntimeException> {
            it.createQuery("SELECT PRO.c_id as \"idProblematica\", P.d_nombres as \"nombreRemitente\", I.a_email_remitente as \"emailRemitente\", " +
                    "I.b_para_interventor as \"paraInterventor\", PRO.a_nombre as \"nombreProblematica\", PRO.a_descripcion as \"descripcionProblematica\", " +
                    "PRO.f_fecha_creacion as \"fechaCreacionProblematica\" " +
                    "FROM PERSONA P, INVITACION I, PROBLEMATICA PRO " +
                    "WHERE P.a_email = I.a_email_remitente AND I.a_email_destinatario = :emailDestinatario AND I.b_vigente = true AND " +
                    "I.c_id_problematica = PRO.c_id AND I.b_rechazada = false")
                    .bind("emailDestinatario", emailDestinatario)
                    .mapToMap()
                    .list()
        }
    }

    /**
     * Transacción que crea un registro en PERSONA_PROBLEMATICA y actualiza otro en INVITACION
     * @param invitacion Con los atributos emailDestinatario, idProblematica, paraInterventor
     * @param idInvitacion
     * @return True si se acepto, False en caso contrario
     */
    fun aceptarInvitacion(invitacion: Invitacion, idInvitacion: String): Boolean {
        return jdbi.inTransaction<Boolean, RuntimeException> { handle ->
            try {
                handle.createUpdate("INSERT INTO PERSONA_PROBLEMATICA(a_id, a_email, c_id_problematica, b_interventor) " + "VALUES(concat(:emailDestinatario, :idProblematica), :emailDestinatario, :idProblematica, :paraInterventor)")
                        .bindBean(invitacion)
                        .execute()

                val seEliminoInvitacion = handle.createUpdate("UPDATE INVITACION SET b_vigente = false WHERE a_email_destinatario = :emailDestinatario AND " + "c_id_problematica = :idProblematica AND a_id = :idInvitacion")
                        .bind("idInvitacion", idInvitacion)
                        .bindBean(invitacion)
                        .execute() > 0
                if (!seEliminoInvitacion) {
                    handle.rollback()
                    false
                } else {
                    true
                }
            } catch (e: UnableToExecuteStatementException) {
                e.printStackTrace()
                handle.rollback()
                false
            }
        }
    }

    /**
     *
     * @param invitacion Con los atributos emailDestinatario, emailRemitente y idProblematica
     * @param idInvitacion
     * @return True si se rechazo, False en caso contrarior
     */
    fun rechazarInvitacion(invitacion: Invitacion, idInvitacion: String): Boolean {
        return jdbi.withHandle<Boolean, RuntimeException> { handle ->
            handle.createUpdate("UPDATE INVITACION SET b_rechazada = true, b_vigente = false " + "WHERE a_email_destinatario = :emailDestinatario AND a_email_remitente = :emailRemitente AND c_id_problematica = :idProblematica")
                    .bindBean(invitacion)
                    .execute() > 0
        }
    }
}