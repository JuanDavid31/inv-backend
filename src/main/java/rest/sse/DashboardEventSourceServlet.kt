package rest.sse

import org.eclipse.jetty.servlets.EventSource
import org.eclipse.jetty.servlets.EventSourceServlet
import javax.servlet.http.HttpServletRequest

class DashboardEventSourceServlet(private val eventPublisher: EventPublisher) : EventSourceServlet() {

    override fun newEventSource(request: HttpServletRequest): EventSource {
        val email = request.getParameter("email")
        val sseEventSource = SseEventSource(eventPublisher)
        val session = request.getSession()
        session.setAttribute("abc", "jaja")
        println("${session.id} - $session")
        eventPublisher.agregarListener(session.id, SessionWrapper(email, session, sseEventSource))
        return sseEventSource
    }
}