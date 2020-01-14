package rest.sse

import org.eclipse.jetty.servlets.EventSource

class SseEventSource(private val eventPublisher: EventPublisher) : EventSource {

    private var emitter: EventSource.Emitter? = null

    override fun onOpen(emitter: EventSource.Emitter){
        this.emitter = emitter
    }

    override fun onClose(){
        eventPublisher.eliminarListener(this)
    }

    public fun emit(datos :String){
        this.emitter!!.data(datos)
    }
}