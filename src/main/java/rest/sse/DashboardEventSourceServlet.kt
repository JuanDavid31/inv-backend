package rest.sse

import org.eclipse.jetty.servlets.EventSource
import org.eclipse.jetty.servlets.EventSourceServlet
import javax.servlet.http.HttpServletRequest

class DashboardEventSourceServlet: EventSourceServlet() {

    override fun newEventSource(request: HttpServletRequest): EventSource {
        val sseEventSource = SseEventSource()
        val session = request.getSession(true)
        DashboardEventPublisher.agregarListener(session.id, sseEventSource)
        return sseEventSource
    }
}