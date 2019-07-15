package ws;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class InteraccionWebsocketServlet extends WebSocketServlet {


    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(1000 * 60 * 10);
        factory.register(EndPoint.class);
    }
}
