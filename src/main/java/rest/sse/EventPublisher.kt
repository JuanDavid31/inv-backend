package rest.sse

import com.fasterxml.jackson.databind.ObjectMapper
import util.SingletonUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpSession

open class EventPublisher() {
    //Companion object -> Crea un singleton en el programa.
    val publishers: MutableMap<String, SessionWrapper>
    val mapper: ObjectMapper

    init {
        publishers = ConcurrentHashMap<String, SessionWrapper>()
        mapper = ObjectMapper()
    }

    fun difundirATodosMenosAUsuarioSolicitante(s: Map<String, Any>) {
        val idSesion = SingletonUtils.darIdSesion() //Si este metodo se realiza en otro hilo la llamada debe moverse.
        val mapper = ObjectMapper()
        publishers.filterKeys { it !== idSesion}
                .forEach { (idSesion, sesionWrapper) -> sesionWrapper.eventSource.emit(mapper.writeValueAsString(s))}
    }

    fun agregarListener(sessionId: String, wrapper: SessionWrapper){
        publishers.put(sessionId, wrapper)
    }

    fun eliminarListener(eventSource: SseEventSource){
        val lista = publishers.toList()
        val pair = lista.find { it.second.sesion.equals(eventSource) }
        pair?.second?.sesion?.invalidate()
        publishers.remove(pair?.first)
    }
}

data class SessionWrapper(val emailUsuario: String, val sesion: HttpSession, val eventSource: SseEventSource)