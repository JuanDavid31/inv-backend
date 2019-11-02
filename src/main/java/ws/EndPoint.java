package ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Grupo;
import entity.SesionCliente;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import usecase.GrupoUseCase;
import util.SingletonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@WebSocket
public class EndPoint {

    private GrupoUseCase grupoUseCase;

    public EndPoint(){
        System.out.println("Instanciando clase EndPoint");
        grupoUseCase = SingletonUtils.darGrupoUseCase();
    }

    @OnWebSocketConnect
    public void onOpen(Session sesion){
        System.out.println("Abriendo sesión - " + String.valueOf(sesion.hashCode()));
        agregarCliente(sesion);
        System.out.println("Sesión abierta!");
    }

    private void agregarCliente(Session sesion) {
        EndPointHandler.agregarCliente(sesion);
    }

    @OnWebSocketMessage
    public void onMessage(Session sesion, String mensajeRecibido) throws IOException {
        System.out.println("Llego un mensaje de " + String.valueOf(sesion.hashCode()) + " : " +mensajeRecibido);
        JsonNode jsonNode = new ObjectMapper().readTree(mensajeRecibido);
        String mensajeADifundir = leerAccion(jsonNode, sesion);
        difundir(sesion, mensajeADifundir);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) throws IOException {
        System.out.println("Cerrando sesion - " + String.valueOf(session.hashCode()));
        String mensajeADifundir = leerAccion(new ObjectMapper().readTree("{ \"accion\":\"Desconectarse\" }"), session);
        difundir(session, mensajeADifundir);
        EndPointHandler.eliminarCliente(session);
    }

    @OnWebSocketError
    public void onError(Session sesion, Throwable throwable){
        System.out.println("Hubo un error con la sesión :" + String.valueOf(sesion.hashCode()));
        throwable.printStackTrace();
        if(throwable instanceof TimeoutException){
            System.out.println("Fue timeout");
        }
    }

    private String leerAccion(JsonNode json, Session session) throws JsonProcessingException {
        switch (json.get("accion").asText()){
            case "Conectarse":
                return agregarDatos(json, session);
            case "Desconectarse":
                return eliminarDatos(json, session);
            case "Agregar":
                agregarGrupo(json);
                break;
            case "Eliminar":
                break;
            case "Juntar nodos":
                return juntarNodos(json);
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
        return "No hay nada";
    }

    private String eliminarDatos(JsonNode json, Session session) throws JsonProcessingException {
        Map<String, SesionCliente> sesionesCliente = EndPointHandler.darSesionesPorGrupo(session);
        SesionCliente sesionCliente = sesionesCliente.get(String.valueOf(session.hashCode()));

        Map nuevoJson = new HashMap<String, Object>();
        nuevoJson.put("accion", "Desconectarse");
        nuevoJson.put("nombre", sesionCliente.getNombre());
        nuevoJson.put("email", sesionCliente.getEmail());
        return new ObjectMapper().writeValueAsString(nuevoJson);
    }

    private String agregarDatos(JsonNode json, Session session) throws JsonProcessingException {
        String nombre = json.get("nombre").asText();
        String email = json.get("email").asText();

        Map<String, SesionCliente> sesionesCliente = EndPointHandler.darSesionesPorGrupo(session);
        SesionCliente sesionCliente = sesionesCliente.get(String.valueOf(session.hashCode()));
        sesionCliente.setNombre(nombre);
        sesionCliente.setEmail(email);
        sesionesCliente.put(String.valueOf(session.hashCode()), sesionCliente);

        Map nuevoJson = new HashMap<String, Object>();
        nuevoJson.put("accion", "Conectarse");
        nuevoJson.put("nombre", nombre);
        nuevoJson.put("email", email);
        return new ObjectMapper().writeValueAsString(nuevoJson);
    }

    private void agregarGrupo(JsonNode json){
        int idProblematica = json.get("idProblematica").asInt();
        String nombreGrupo = json.get("nombreGrupo").asText();
        Grupo grupoCreado = grupoUseCase.agregarGrupo(idProblematica, new Grupo(nombreGrupo));

        //TODO: Reenviar a todos

    }

    private String juntarNodos(JsonNode json) {
        return json.toString();
    }

    private String bloquearNodo(JsonNode json) throws JsonProcessingException {
        return json.toString();
    }

    private String moverNodo(JsonNode json) throws JsonProcessingException {
        return json.toString();
    }

    private String desbloquearNodo(JsonNode json) throws JsonProcessingException {
        return json.toString();
    }

    private void difundir(Session sesion, String mensaje) {
        Map<String, SesionCliente> endPoints = EndPointHandler.darSesionesPorGrupo(sesion);
        for (Map.Entry<String, SesionCliente> entry : endPoints.entrySet()) {
            String hashActual = String.valueOf(entry.getValue().getSesion().hashCode());
            String hashSesion = String.valueOf(sesion.hashCode());
            if (!hashActual.equals(hashSesion)) {
                try {
                    SesionCliente value = entry.getValue();
                    if (value != null) {
                        RemoteEndpoint remote = value.getSesion().getRemote();
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
