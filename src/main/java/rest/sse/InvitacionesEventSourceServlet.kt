package rest.sse

class InvitacionesEventSourceServlet: EventSourceServlet() {

    override fun newEventSource(request: HttpServletRequest): EventSource {
        val sseEventSource = SseEventSource()
        val session = request.getSession(true)
        DashboardEventPublisher.agregarListener(session.id, SessionWrapper(session, sseEventSource))
        return sseEventSource
    }
}