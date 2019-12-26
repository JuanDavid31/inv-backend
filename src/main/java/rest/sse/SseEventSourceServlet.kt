package rest.sse

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.jetty.servlets.EventSource
import org.eclipse.jetty.servlets.EventSourceServlet
import javax.servlet.http.HttpServletRequest


class SseEventSourceServlet: EventSourceServlet() {

    override fun newEventSource(request: HttpServletRequest): EventSource? {
        println("Llego un nuevo EventSource")
        val sseEventSource = SseEventSource()
        EventPublisher.agregarListener(sseEventSource)
        GlobalScope.launch {
            for(i in 1..5){
                delay(1000)
                EventPublisher.publish("Buenas - $i")
            }
        }
        return sseEventSource
    }
}