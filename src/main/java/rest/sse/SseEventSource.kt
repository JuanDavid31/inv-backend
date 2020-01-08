package rest.sse

import org.eclipse.jetty.servlets.EventSource

class SseEventSource: EventSource {


    private var emitter: EventSource.Emitter? = null

    override fun onOpen(emitter: EventSource.Emitter){
        this.emitter = emitter
    }

    override fun onClose(){
        EventPublisher.eliminarListener(this)
    }

    public fun emit(datos :String){
        this.emitter!!.event("elname", "Ladata")
        this.emitter!!.data(datos)
        this.emitter!!.comment("elComment")
    }

    public fun emit2(datos: String){
        this.emitter!!.data("data: message"+datos+"\n\n")
    }
}