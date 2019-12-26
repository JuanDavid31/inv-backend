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
        return sseEventSource
    }
}