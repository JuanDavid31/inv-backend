package rest.sse

import com.fasterxml.jackson.databind.ObjectMapper
import entity.Mensaje
import java.util.*

class EventPublisher {

    companion object{ //Crea un singleton en todo el programa.
        private val publishers: MutableList<SseEventSource> = Collections.synchronizedList(ArrayList<SseEventSource>())

        fun publish(mensaje: Map<String, Any>) {
            val mapper = ObjectMapper()
            publishers.forEach { it.emit(mapper.writeValueAsString(mensaje)) }
        }

        fun agregarListener(eventPublisher: SseEventSource){
            publishers.add(eventPublisher)
        }

        fun eliminarListener(eventPublisher: SseEventSource){
            publishers.remove(eventPublisher)
        }
    }
}