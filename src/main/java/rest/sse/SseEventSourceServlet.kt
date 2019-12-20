package rest.sse

import javax.servlet.http.HttpServletRequest

import org.eclipse.jetty.servlets.EventSource
import org.eclipse.jetty.servlets.EventSourceServlet

class SseEventSourceServlet: EventSourceServlet() {

    override fun newEventSource(request: HttpServletRequest): EventSource? {
        println("Algo sucede")
        val sseEventSource = SseEventSource()
        EventPublisher.agregarListener(sseEventSource)
        return sseEventSource
    }
}