package rest.sse

import org.eclipse.jetty.servlets.EventSource
import org.eclipse.jetty.servlets.EventSourceServlet
import javax.servlet.http.HttpServletRequest

class InvitacionesEventSourceServlet(private val eventPublisher: EventPublisher) : EventSourceServlet() {

    override fun newEventSource(request: HttpServletRequest): EventSource {
        val email = request.getParameter("email")
        val sseEventSource = SseEventSource(eventPublisher)
        val session = request.getSession(true)
        eventPublisher.agregarListener(session.id, SessionWrapper(email, session, sseEventSource))
        return sseEventSource
    }
}