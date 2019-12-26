package ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import entity.Sala;
import entity.SesionCliente;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.*;
import util.SingletonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebSocket
public class EndPoint {

    public EndPoint(){
        System.out.println("Instanciando clase EndPoint");
        EndPointHandler.grupoUseCase = SingletonUtils.darGrupoUseCase();
        EndPointHandler.nodoUseCase = SingletonUtils.darNodoUseCase();
    }

    @OnWebSocketConnect
    public void onOpen(Session sesion){
        System.out.println("Abriendo sesión - " + sesion.hashCode());
        agregarCliente(sesion);
        System.out.println("Sesión abierta!");
    }

    private void agregarCliente(Session sesion) {
        EndPointHandler.agregarCliente(sesion);
    }

    @OnWebSocketMessage
    public void onMessage(Session sesion, String mensajeRecibido) throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(mensajeRecibido);
        String mensajeADifundir = leerAccion(jsonNode, sesion);
        difundir(sesion, mensajeADifundir);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) throws IOException {
        System.out.println("Cerrando sesion - " + session.hashCode());
        String mensajeADifundir = eliminarDatos(session);
        difundir(session, mensajeADifundir);
        EndPointHandler.eliminarCliente(session);
    }

    @OnWebSocketError
    public void onError(Session sesion, Throwable throwable){
        System.out.println("Hubo un error con la sesión :" + sesion.hashCode());

        if(throwable instanceof TimeoutException){
            System.out.println("Un cliente se desconecto debido al timeout");
        }else{
            throwable.printStackTrace();
        }
    }

    private String leerAccion(JsonNode json, Session session) {
        switch (json.get("accion").asText()){
            case "Conectarse":
                return agregarDatos(json, session);
            case "Agregar elemento":
                return agregarElemento(json, session);
            case "Mover elemento":
                return moverElemento(json, session);
            case "Eliminar elemento":
                return eliminarElemento(json, session);
            case "Bloquear":
                return bloquearNodo(json);
            case "Desbloquear":
                return desbloquearNodo(json);
            case "Mover":
                return moverNodo(json, session);
            case "Mover padre":
                return moverNodoPadre(json, session);
            case "Cambio solicitud de organizacion":
                return cambioSolicitudOrganizacion(json, session);
            case "Cambiar nombre":
                return cambiarNombre(json, session);
            default:
                return "No hay nada";
        }
    }

    private String conectarGrupos(JsonNode json, Session session) {
        JsonNode nuevaConexion = json.get("edge");
        String edgeId = nuevaConexion.get("data").get("id").asText();
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();

        nodos.put(edgeId, nuevaConexion);

        sala.getGruposAgregados().put(edgeId, nuevaConexion);

        return json.toString();
    }

    private String desconectarGrupos(JsonNode json, Session session) {
        JsonNode conexionAEliminar = json.get("edge");
        String edgeId = conexionAEliminar.get("data").get("id").asText();
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();

        nodos.remove(edgeId);

        if(sala.getGruposAgregados().containsKey(edgeId)){
            sala.getGruposAgregados().remove(edgeId);
        }else{
            sala.getGruposEliminados().put(edgeId, conexionAEliminar);
            sala.getNodos().remove(edgeId);
        }

        return json.toString();
    }

    private String juntarNodos(JsonNode json, Session session) {
        Sala sala = EndPointHandler.darSala(EndPointHandler.extraerIdSala(session));
        Map<String, JsonNode> nodos = sala.getNodos();

        JsonNode nodoPadre = json.get("nodoPadre");
        JsonNode nodoEntrante = json.get("nodo");
        JsonNode nodoVecino = json.get("nodoVecino");

        String idNodoEntrante = nodoEntrante.get("data").get("id").asText();
        String idNodoPadre = nodoPadre.get("data").get("id").asText();

        if(!nodos.containsKey(idNodoPadre)){ //No existe el padre en los nodos de la sala
            nodos.put(idNodoPadre, nodoPadre);


            //TODO: Nuevo codigo
            sala.getGruposAgregados().put(idNodoPadre, nodoPadre);
        }


        ((ObjectNode)nodos.get(idNodoEntrante).get("data")).set("parent", new TextNode(idNodoPadre));

        //El nodo vecino no esta vacio. Esta vacio en caso de que en el grupo hayan quedado 2 nodos o más, por tanto no hay un solo vecino.
        if(nodoVecino.fieldNames().hasNext()){
            String idNodoVecino = nodoVecino.get("data").get("id").asText();
            ((ObjectNode)nodos.get(idNodoVecino).get("data")).set("parent", new TextNode(idNodoPadre));
        }

        return json.toString();
    }

    private String separarNodos(JsonNode json, Session session) {
        Sala sala = EndPointHandler.darSala(EndPointHandler.extraerIdSala(session));
        Map<String, JsonNode> nodos = sala.getNodos();

        JsonNode nodoSaliente = json.get("nodo");
        JsonNode nodoVecino = json.get("nodoVecino");

        String idNodoSaliente = nodoSaliente.get("data").get("id").asText();

        //Elimino el padre del nodo saliente
        ((ObjectNode)nodos.get(idNodoSaliente).get("data")).set("parent", null);

        //nodoVecino esta vacio en caso de que en el grupo hayan quedado 2 nodos o más, por tanto no hay un solo vecino.
        if(!nodoVecino.fieldNames().hasNext()) return json.toString();

        String idNodoVecino = nodoVecino.get("data").get("id").asText();
        String idNodoPadre = nodoVecino.get("data").get("parent").asText();
        ((ObjectNode)nodos.get(idNodoVecino).get("data")).set("parent", null);

        nodos.remove(idNodoPadre);

        //TODO: Nuevo codigo.
        if(sala.getGruposAgregados().containsKey(idNodoPadre)){
            sala.getGruposAgregados().remove(idNodoPadre);
        }else{
            sala.getGruposEliminados().put(idNodoPadre, new ObjectMapper().createObjectNode());
        }

        return json.toString();
    }

    private String eliminarDatos(Session session) throws JsonProcessingException {
        Map<String, SesionCliente> sesionesCliente = EndPointHandler.darSesionesPorSala(EndPointHandler.extraerIdSala(session));
        SesionCliente sesionCliente = sesionesCliente.get(String.valueOf(session.hashCode()));

        HashMap<String, Object> nuevoJson = new HashMap();
        nuevoJson.put("accion", "Desconectarse");
        nuevoJson.put("nombre", sesionCliente.getNombre());
        nuevoJson.put("email", sesionCliente.getEmail());
        return new ObjectMapper().writeValueAsString(nuevoJson);
    }

    private String agregarDatos(JsonNode json, Session session) {
        actualizarSesionClienteVacia(json, session);

        enviarNodosACliente(session);

        return json.toString();
    }

    private String agregarElemento(JsonNode json, Session session) {
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();
        JsonNode elemento = json.get("elemento");
        if(elemento.get("data").get("source") != null){ //Es grupo
            nodos.put(elemento.get("data").get("id").asText(), elemento);
            sala.getGruposAgregados().put(elemento.get("data").get("id").asText(), elemento);
        }else{ //Es edge
            String edgeId = elemento.get("data").get("id").asText();

            nodos.put(edgeId, elemento);
            sala.getGruposAgregados().put(edgeId, elemento);
        }
        return json.toString();
    }

    private String moverElemento(JsonNode json, Session session) {
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();
        JsonNode elemento = json.get("elemento");

        String id = elemento.get("data").get("id").asText();
        JsonNode parent = elemento.get("data").get("parent");

        ((ObjectNode)nodos.get(id).get("data")).set("parent", parent != null ? parent : NullNode.getInstance());

        return json.toString();
    }

    private String eliminarElemento(JsonNode json, Session session) {
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();
        JsonNode elemento = json.get("elemento");
        String id = elemento.get("data").get("id").asText();

        nodos.remove(id);

        if(sala.getGruposAgregados().containsKey(id)){
            sala.getGruposAgregados().remove(id);
        }else{
            sala.getGruposEliminados().put(id, new ObjectMapper().createObjectNode());
        }

        return json.toString();
    }

    private void enviarNodosACliente(Session session) {
        Sala sala = EndPointHandler.darSala(EndPointHandler.extraerIdSala(session));
        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("accion", "Nodos");
        jsonMap.put("nodos", sala.getNodos().values());

        try {
            String jsonString = new ObjectMapper().writeValueAsString(jsonMap);
            difundirA(jsonString, session);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la sesion del cliente que se creo con valores vacios.
     * @param json
     * @param session
     */
    private void actualizarSesionClienteVacia(JsonNode json, Session session){
        String nombre = json.get("nombre").asText();
        String email = json.get("email").asText();
        boolean solicitandoOrganizacion = json.get("solicitandoOrganizacion").asBoolean();

        Map<String, SesionCliente> sesionesCliente = EndPointHandler.darSesionesPorSala(EndPointHandler.extraerIdSala(session));
        SesionCliente sesionCliente = sesionesCliente.get(String.valueOf(session.hashCode()));
        sesionCliente.setNombre(nombre);
        sesionCliente.setEmail(email);
        sesionCliente.setSolicitandoOrganizacion(solicitandoOrganizacion);
        sesionesCliente.put(String.valueOf(session.hashCode()), sesionCliente);
    }

    private void difundirA(String mensaje, Session session){
        try {
            session.getRemote().sendString(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String bloquearNodo(JsonNode json) {
        return json.toString();
    }

    private String moverNodo(JsonNode json, Session session) {
        EndPointHandler.actualizarPosicionNodo(json.get("elemento"), session);
        return json.toString();
    }

    private String moverNodoPadre(JsonNode json, Session session) {
        /*EndPointHandler.actualizarPosicionNodo(json.get("nodo"), session);*/
        JsonNode nodosHijos = json.get("nodosHijos");
        for(JsonNode nodo : nodosHijos){
            EndPointHandler.actualizarPosicionNodo(nodo, session);
        }
        ((ObjectNode)json).set("accion", new TextNode("Mover"));
        return json.toString();
    }

    private String desbloquearNodo(JsonNode json) {
        return json.toString();
    }

    private String cambioSolicitudOrganizacion(JsonNode json, Session session) {
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        String email = json.get("email").asText();
        boolean solicitandoOrganizacion = json.get("solicitandoOrganizacion").asBoolean();

        sala.getClientes()
            .values()
            .stream()
            .filter(sesionCliente -> sesionCliente.getEmail().equals(email))
            .findFirst()
            .get()
            .setSolicitandoOrganizacion(solicitandoOrganizacion);

        int solicitantes = sala.getClientes()
                .values()
                .stream()
                .filter(sesionCliente -> sesionCliente.getSolicitandoOrganizacion())
                .collect(Collectors.toList())
                .size();

        int solicitantesTotales = sala.getClientes().values().size();

        if(solicitantes == solicitantesTotales){
            //Envio mensaje de reinicio a la sesión actual.
            ObjectNode mensajeIniciarReinicio = new ObjectMapper().createObjectNode();
            mensajeIniciarReinicio.set("accion", new TextNode("Iniciar reinicio"));
            difundirA(mensajeIniciarReinicio.toString(), session);

            //Envio reinicio de solicitudes a todos los nodos menos el actual.
            ObjectNode mensajeReiniciarSolicitudes = new ObjectMapper().createObjectNode();
            mensajeReiniciarSolicitudes.set("accion", new TextNode("Reiniciar solicitudes"));
            difundir(session, mensajeReiniciarSolicitudes.toString());

            //Cambiar las solicitudes actuales.
            sala.getClientes()
                .values()
                .stream()
                .forEach(sesionCliente -> sesionCliente.setSolicitandoOrganizacion(false));

            return "";
        }else{
            return json.toString();
        }
    }

    private String cambiarNombre(JsonNode json, Session session){
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();
        Map<String, JsonNode> gruposAgregados = sala.getGruposAgregados();

        String id = json.get("data").get("id").asText();
        String nuevoNombre = json.get("data").get("nombre").asText();


        ((ObjectNode) nodos.get(id).get("data")).replace("nombre", new TextNode(nuevoNombre));

        if(gruposAgregados.containsKey(id)){
            ((ObjectNode) gruposAgregados.get(id).get("data")).replace("nombre", new TextNode(nuevoNombre));
        }

        return json.toString();
    }

    private void difundir(Session sesion, String mensaje) {
        int idSala = EndPointHandler.extraerIdSala(sesion);
        Map<String, SesionCliente> endPoints = EndPointHandler.darSesionesPorSala(idSala);

        endPoints.forEach((key, value) -> {
            String hashActual = String.valueOf(value.getSesion().hashCode());
            String hashSesion = String.valueOf(sesion.hashCode());
            if (!hashActual.equals(hashSesion)) {
                try {
                    RemoteEndpoint remote = value.getSesion().getRemote();
                    remote.sendString(mensaje);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WebSocketException ex) {
                    System.out.println("Error inesperado, eliminado cliente");
                    EndPointHandler.eliminarCliente(sesion);
                }
            }
        });
    }
}
