package rest.sse

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.HashMap

class InvitacionesEventPublisher : EventPublisher() {

    /**
     * Le envia una notificación al usuario invitado siempre y cuando
     * este en linea.
     * @param jsonHash JSON-like datos a enviar al usuario.
     * @param emailDestinatario Email de la persona que se esta invitando
     */
    fun enviarInvitacion(jsonHash: HashMap<String, Any>, emailDestinatario: String) {
        publishers.toList()
            .map { (_, sessionWrapper) -> sessionWrapper }
            .filter { it.emailUsuario == emailDestinatario } //El usuario conectado puede estar activo en varias ventanas del navegador.
            .map { it.eventSource }
            .forEach {  it?.emit(ObjectMapper().writeValueAsString(jsonHash)) }
    }

    /**
     * Difunde el evento a todos los interventos que pertenezcan a la problematica
     * y se encuentren conectados, si el usuario que inicio la acción de difusión, fue invitado como interventor
     * y este acepto la misma entonces es omitido gracias al idSesion.
     * @param idSesion Sesion de la persona que respondio la invitación y así mismo disparo la difusión
     * @param datos JSON-like que contiene los datos que se enviaran a cada interventor
     * @param emailInterventores lista de todos los interventores de la problematica a los que se notificara del evento.
     */
    fun difundirEventoDeRespuestaAInterventores(sesionId: String, jsonHash: HashMap<String, Any>, emailInterventores: List<String>) {
        publishers.filterKeys { it !== sesionId}
            .map { (_, sessionWrapper) -> sessionWrapper}
            .filter { emailInterventores.contains(it.emailUsuario) }
            .forEach { it.eventSource.emit(mapper.writeValueAsString(jsonHash))}
    }
}