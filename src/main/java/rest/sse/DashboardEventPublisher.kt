package rest.sse

import com.fasterxml.jackson.databind.ObjectMapper
import util.SingletonUtils
import java.util.*

class DashboardEventPublisher {

    companion object{ //Crea un singleton en todo el programa.
        private val publishers: MutableMap<String, SseEventSource> = Collections.synchronizedMap(HashMap<String, SseEventSource>())

        fun publish(s: Map<String, Any>) {
            //TODO: Usar clase estatica para obtener el sessionId actual.
            val idSesion = SingletonUtils.darIdSesion()
            val mapper = ObjectMapper()
            publishers.filterKeys { it !== idSesion}
                    .forEach { it.emit(mapper.writeValueAsString(s))}
        }

        fun agregarListener(sessionId: String, eventPublisher: SseEventSource){
            publishers.put(sessionId, eventPublisher)
        }

        fun eliminarListener(eventSource: SseEventSource){
            val lista = publishers.toList


            //TODO: publishers.remove(sessionId)
        }
    }
}