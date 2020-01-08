package rest.sse

import java.util.*

class EventPublisher {

    companion object{ //Crea un singleton en todo el programa.
        val publishers: MutableList<SseEventSource> = Collections.synchronizedList(ArrayList<SseEventSource>())

        fun publish(s: String) {
            publishers.forEach {
                it.emit(s)
            }
        }

        fun agregarListener(eventPublisher: SseEventSource){
            publishers.add(eventPublisher)
        }

        fun eliminarListener(eventPublisher: SseEventSource){
            publishers.remove(eventPublisher)
        }
    }
}