package rest.sse

import java.util.*

class EventPublisher {

    companion object{
        val listeners: MutableList<SseEventSource> = Collections.synchronizedList(ArrayList<SseEventSource>())

        fun publish(){
            listeners.forEach { println(it) }
        }

        fun agregarListener(eventPublisher: SseEventSource){
            listeners.add(eventPublisher)
        }

        fun eliminarListener(eventPublisher: SseEventSource){
            listeners.remove(eventPublisher)
        }
    }
}