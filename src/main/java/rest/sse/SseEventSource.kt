package rest.sse

import org.eclipse.jetty.servlets.EventSource

class SseEventSource: EventSource {


    private var emitter: EventSource.Emitter? = null
    private var id: String = ""

    override fun onOpen(emitter: EventSource.Emitter){
        this.emitter = emitter
        println("Se conecto algo")
    }

    override fun onClose(){
        EventPublisher.eliminarListener(this)
    }

    public fun emit(datos :String){
        this.emitter!!.data(datos)
    }
}