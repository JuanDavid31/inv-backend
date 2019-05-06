package ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Map;

@WebSocket
public class EndPoint {

    public Session sesion;

    @OnWebSocketConnect
    public void onOpen(Session sesion){
        System.out.println("Abriendo sesión...");
        this.sesion = sesion;
        EndPointHandler.endPoints.put(String.valueOf(sesion.hashCode()), this);
        System.out.println("Sesión abierta!");
    }

    @OnWebSocketMessage
    public void onMessage(Session sesion, String mensaje) throws IOException {
        if(this.sesion.equals(sesion)) {
            System.out.println("Las sesiones son iguales!");
        }else{
            System.out.println("Las sesiones NO son iguales... :(");
        }
        System.out.println("Llego un mensaje de " + sesion.getRemoteAddress().getHostName() + " : " +mensaje);

        Map<String, EndPoint> endPoints = EndPointHandler.endPoints;
        endPoints.entrySet().stream()
            .filter(e -> !String.valueOf(e.getValue().sesion.hashCode()).equals(String.valueOf(sesion.hashCode())))
            .forEach(e -> {
                try {
                    e.getValue().sesion.getRemote().sendString("Desde " + sesion.getRemoteAddress().getHostName() + " : " + mensaje);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

    }

    @OnWebSocketClose
    public void onClose(int code, String mensaje){
        System.out.println("Cerrando sesion");
        EndPointHandler.endPoints.remove(String.valueOf(sesion.hashCode()));
    }

    @OnWebSocketError
    public void onError(Session sesion, Throwable throwable){
        System.out.println("Hubo un error con la sesión :" + String.valueOf(sesion.hashCode()));
    }
}
