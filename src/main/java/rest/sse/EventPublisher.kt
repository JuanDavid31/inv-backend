package rest.sse

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpSession

open class EventPublisher() {
    //Companion object -> Crea un singleton en el programa.

    private val publishers: MutableMap<String, SessionWrapper> = ConcurrentHashMap()
    val mapper: ObjectMapper = ObjectMapper()

    fun agregarListener(sessionId: String, wrapper: SessionWrapper){
        publishers.put(sessionId, wrapper)
    }

    fun eliminarListener(eventSource: SseEventSource){
        val lista = publishers.toList()
        val pair = lista.find { it.second.eventSource.equals(eventSource) }
        val sesionString = pair?.second?.sesion?.id
        pair?.second?.sesion?.invalidate()
        val seBorro = publishers.remove(pair?.first)
        println("${seBorro != null} al borrar ${pair?.second?.emailUsuario} con sesion $sesionString")
    }

    /*INVITACIONES*/

    /**
     * Le envia una notificación al usuario invitado siempre y cuando
     * este en linea.
     * @param jsonHash JSON-like datos a enviar al usuario.
     * @param emailDestinatario Email de la persona que se esta invitando
     */
    fun enviarInvitacion(jsonHash: Map<String, Any?>, emailDestinatario: String) {
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

    /*DASHBOARD*/


    /**
     * Difunde el evento a todos los participantes que pertenezcan a la problematica
     * y se encuentre conectados, el usuario que inicio la acción de difusión es omitido de esta
     * misma.
     * @param idSesion Sesion de la persona que disparo la difusión
     * @param datos JSON-like que contiene los datos que se enviaran a cada participante
     * @param participantes lista de todos los participantes que participan en la problematica.
     */
    fun difundirAvanceFaseAParticipantesMenosA(idSesion: String, datos: Map<String, Any>, participantes: List<String>){
        publishers.filterKeys { it !== idSesion}
                .map { (_, sessionWrapper) -> sessionWrapper}
                .filter { sessionWrapper -> participantes.contains(sessionWrapper.emailUsuario) }
                .forEach { sesionWrapper -> sesionWrapper.eventSource.emit(mapper.writeValueAsString(datos))}
    }

}

data class SessionWrapper(val emailUsuario: String, val sesion: HttpSession, val eventSource: SseEventSource)