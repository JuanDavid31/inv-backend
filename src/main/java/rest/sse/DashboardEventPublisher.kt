package rest.sse

import com.fasterxml.jackson.databind.ObjectMapper
import util.SingletonUtils
import java.util.*

class DashboardEventPublisher {

    companion object{ //Crea un singleton en el programa.
        private val publishers: MutableMap<String, SessionWrapper> = Collections.synchronizedMap(HashMap<String, SessionWrapper>())

        fun publish(s: Map<String, Any>) {
            val idSesion = SingletonUtils.darIdSesion()
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
            pair?.second?.sesion.invalidate()
            publishers.remove(pair?.first)
        }
    }
}

data class SessionWrapper(val sesion: HttpSession, val eventSource: SseEventSource)