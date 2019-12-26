package rest.sse

import java.util.*

class EventPublisher {

    companion object{ //Crea un singleton en todo el programa.
        val listeners: MutableList<SseEventSource> = Collections.synchronizedList(ArrayList<SseEventSource>())

        fun publish(s: String) {
            listeners.forEach {
                println(it)
                it.emit(s)
            }
        }

        fun agregarListener(eventPublisher: SseEventSource){
            listeners.add(eventPublisher)
        }

        fun eliminarListener(eventPublisher: SseEventSource){
            listeners.remove(eventPublisher)
        }
    }
}