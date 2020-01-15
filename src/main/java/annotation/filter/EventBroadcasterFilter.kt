package annotation.filter

import io.dropwizard.jersey.sessions.Session
import util.SingletonUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Context

@ServerEventBroadcaster
class EventBroadcasterFilter: ContainerRequestFilter {

    @Context
    var request: HttpServletRequest? = null

    override fun filter(requestContext: ContainerRequestContext) {
        if(request != null){
            System.out.println("Si hay request")
            val session = request?.session
            println("${session?.id} - $session - ${session?.getAttribute("abc")}")
            val algo = session?.id
            SingletonUtils.guardarIdSesion(algo)
        }else{
            System.out.println("No hay request")
        }
    }
}