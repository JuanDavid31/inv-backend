package ws;

import dao.DaoGrupo;
import dao.DaoNodo;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class InteraccionWebsocketServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(1000 * 60 * 10); //10 minutos
        factory.register(EndPoint.class);
    }
}
