package ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Grupo;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import usecase.GrupoUseCase;
import util.SingletonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class EndPoint {

    private GrupoUseCase grupoUseCase;

    public EndPoint(){
        System.out.println("Instnaciando clase EndPoint");
        grupoUseCase = SingletonUtils.darGrupoUseCase();
    }

    @OnWebSocketConnect
    public void onOpen(Session sesion){
        System.out.println("Abriendo sesión...");
        System.out.println(sesion);
        agregarCliente(sesion);
        System.out.println("Sesión abierta!");
    }

    private void agregarCliente(Session sesion) {
        EndPointHandler.agregarCliente(sesion);
    }

    @OnWebSocketMessage
    public void onMessage(Session sesion, String mensajeRecibido) throws IOException {
        System.out.println("Llego un mensaje de " + sesion.getRemoteAddress().getHostName() + " : " +mensajeRecibido);
        JsonNode jsonNode = new ObjectMapper().readTree(mensajeRecibido);
        String mensajeADifundir = leerAccion(jsonNode, sesion);
        difundir(sesion, mensajeADifundir);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason){
        System.out.println("Cerrando sesion");
        EndPointHandler.eliminarCliente(session);
    }

    @OnWebSocketError
    public void onError(Session sesion, Throwable throwable){
        System.out.println("Hubo un error con la sesión :" + String.valueOf(sesion.hashCode()));
        throwable.printStackTrace();
    }

    private String leerAccion(JsonNode json, Session session) throws JsonProcessingException {
        switch (json.get("accion").asText()){
            case "Agregar":
                agregarGrupo(json);
                break;
            case "Eliminar":
                break;
            case "Apadrinar":
                break;
            case "Desapadrinar":
                break;
            case "Bloquear":
                return bloquearNodo(json);
            case "Mover":
                return moverNodo(json);
            case "Desbloquear":
                return desbloquearNodo(json);
            default:
                return null;
        }
        return null;
    }

    private void agregarGrupo(JsonNode json){
        int idProblematica = json.get("idProblematica").asInt();
        String nombreGrupo = json.get("nombreGrupo").asText();
        Grupo grupoCreado = grupoUseCase.agregarGrupo(idProblematica, new Grupo(nombreGrupo));

        //TODO: Reenviar a todos

    }

    private String bloquearNodo(JsonNode json) throws JsonProcessingException {
        Map nuevoJson = new HashMap<String, Object>();
        nuevoJson.put("mensaje", "Bloquear");
        nuevoJson.put("idNodo", json.path("idNodo").asLong());
        return new ObjectMapper().writeValueAsString(nuevoJson);
    }

    private String moverNodo(JsonNode json) throws JsonProcessingException {
        Map nuevoJson = new HashMap<String, Object>();
        nuevoJson.put("mensaje", "Mover");
        nuevoJson.put("idNodo", json.path("idNodo").asLong());
        return new ObjectMapper().writeValueAsString(nuevoJson);
    }

    private String desbloquearNodo(JsonNode json) throws JsonProcessingException {
        Map nuevoJson = new HashMap<String, Object>();
        nuevoJson.put("mensaje", "Desbloquear");
        nuevoJson.put("idNodo", json.path("idNodo").asLong());
        return new ObjectMapper().writeValueAsString(nuevoJson);
    }

    private void difundir(Session sesion, String mensaje) {
        Map<String, Session> endPoints = EndPointHandler.darSesionesPorGrupo(sesion);
        for (Map.Entry<String, Session> session : endPoints.entrySet()) {
            if (!String.valueOf(session.getValue().hashCode()).equals(String.valueOf(sesion.hashCode()))) {
                try {
                    Session value = session.getValue();
                    if (value != null) {
                        RemoteEndpoint remote = value.getRemote();
                        if(remote != null){
                            remote.sendString(mensaje);
                        }else{
                            System.out.println("RemoteEndPoint is " + null);
                        }
                        System.out.println("Enviando " + mensaje);
                    } else {
                        System.out.println("La sesión es nula");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
