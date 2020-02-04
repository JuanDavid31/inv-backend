package annotation.filter

import util.SingletonUtils
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Context

@ServerEventBroadcaster
class EventBroadcasterFilter: ContainerRequestFilter {

    @Context
    var request: HttpServletRequest? = null

    override fun filter(requestContext: ContainerRequestContext) {
        val session = request?.session

        println("EventBroadcastFilter")
        println("${session?.id} - $session")
        println("abc : ${session?.getAttribute("abc")}")

        SingletonUtils.guardarIdSesion(session?.id)
    }
}