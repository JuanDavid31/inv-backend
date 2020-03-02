package ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.gson.JsonObject;
import entity.Grupo;
import entity.Relacion;
import entity.Sala;
import entity.SesionCliente;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.*;
import util.SingletonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebSocket
public class EndPoint {

    public EndPoint(){
        EndPointHandler.grupoUseCase = SingletonUtils.darGrupoUseCase();
        EndPointHandler.nodoUseCase = SingletonUtils.darNodoUseCase();
        EndPointHandler.relacionUseCase = SingletonUtils.darRelacionUseCase();
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
        difundirATodosMenosA(sesion, mensajeADifundir);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) throws IOException {
        System.out.println("Cerrando sesion - " + session.hashCode());
        String mensajeADifundir = eliminarDatos(session);
        difundirATodosMenosA(session, mensajeADifundir);
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

    private synchronized String leerAccion(JsonNode json, Session session) {
        switch (json.get("accion").asText()){
            case "Conectarse":
                return agregarDatos(json, session);
            case "Actualizar posiciones": //Esta acción SOLO SALE del cliente
                return actualizarPosiciones(json, session);
            case "Agregar elemento":
                return agregarElemento(json, session);
            case "Mover elemento":
                return moverElemento(json, session);
            case "Eliminar elemento":
                return eliminarElemento(json, session);
            case "Bloquear":
                return bloquearNodos(json, session);
            case "Desbloquear":
                return desbloquearNodos(json, session);
            case "Mover":
                return moverNodo(json, session);
            case "Mover padre":
                return moverNodoPadre(json, session); //Movimiento en x & Y.
            case "Cambio solicitud de organizacion":
                return cambioSolicitudOrganizacion(json, session);
            case "Cambiar nombre":
                return cambiarNombre(json, session);
            default:
                return "{}";
        }
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

    private String actualizarPosiciones(JsonNode json, Session session) {
        JsonNode nodos = json.get("nodos");
        for(JsonNode nodo : nodos){ EndPointHandler.actualizarPosicionNodo(nodo, session);}
        EndPointHandler.guardarPosicionesIniciales(nodos, session);
        return json.toString();
    }

    private String agregarElemento(JsonNode json, Session session) {
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();
        JsonNode elemento = json.get("elemento");

        nodos.put(elemento.get("data").get("id").asText(), elemento);
        switch (json.get("tipo").asText()){
            case "Grupo":
                persistirYReenviarGrupo(elemento, idSala, session);
                break;
            case "Edge grupo":
                persistirEdgeGrupo(elemento);
                break;
            case "Edge nodo":
                persistirEdgeNodo(elemento);
                break;
            default: break;
        }

        return json.toString();
    }

    private void persistirYReenviarGrupo(JsonNode elemento, int idProblematica, Session session){
        String idProvicional = elemento.get("data").get("id").asText();
        String nombreGrupo = elemento.get("data").get("nombre").asText();
        CompletableFuture.supplyAsync(() -> {
            System.out.println("Inicia la inserción del nuevo grupo");
            return EndPointHandler.agregarGrupo(idProblematica, new Grupo(nombreGrupo));
        }).thenAcceptAsync(grupo -> {
            System.out.println("Termina la inserción del nuevo grupo");
            HashMap nuevoJson = new HashMap();
            nuevoJson.put("accion", "Actualizar id grupo");
            nuevoJson.put("id", idProvicional);
            nuevoJson.put("nuevoId", grupo.getId());
            try {
                actualizarIdGrupo(session, idProvicional, grupo.getId());
                difundirATodos(session, new ObjectMapper().writeValueAsString(nuevoJson));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    private void actualizarIdGrupo(Session session, String idProvicional, int idNuevo){
        Sala sala = EndPointHandler.darSala(EndPointHandler.extraerIdSala(session));
        JsonNode grupo = sala.getNodos().get(idProvicional);
        ((ObjectNode)grupo.get("data")).set("id", new TextNode(Integer.toString(idNuevo))); //Actualizamos el id del elemento.
        ((ObjectNode)grupo.get("data")).set("idAntiguo", new TextNode(idProvicional));
        sala.getNodos().remove(idProvicional);
        sala.getNodos().put(Integer.toString(idNuevo), grupo);
    }

    private void persistirEdgeGrupo(JsonNode edge){
        Relacion relacion = jsonNodeAEdgeGrupo(edge);
        EndPointHandler.agregarRelacionGrupoAGrupo(relacion);
    }

    private Relacion jsonNodeAEdgeGrupo(JsonNode edge){
        String idPadre = edge.get("data").get("source").asText();
        String id = edge.get("data").get("target").asText();
        Relacion relacion = new Relacion();
        relacion.setIdGrupo(Integer.parseInt(id));
        relacion.setIdGrupoPadre(Integer.parseInt(idPadre));
        relacion.setFase(2);
        return relacion;
    }

    private void persistirEdgeNodo(JsonNode edge){
        Relacion relacion = jsonNodeAEdgeNodo(edge);
        EndPointHandler.agregarRelacionNodoANodo(relacion);
    }

    private Relacion jsonNodeAEdgeNodo(JsonNode edge){
        String idPadre = edge.get("data").get("source").asText();
        String id = edge.get("data").get("target").asText();
        Relacion relacion = new Relacion();
        relacion.setIdNodo(Integer.parseInt(id));
        relacion.setIdNodoPadre(Integer.parseInt(idPadre));
        relacion.setFase(2);
        return relacion;
    }

    private String moverElemento(JsonNode json, Session session) {
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();
        JsonNode elemento = json.get("elemento");

        String id = elemento.get("data").get("id").asText();
        JsonNode parent = elemento.get("data").get("parent");

        agregarOEliminarRelacionEntreNodoYGrupo(id, parent, session);

        ((ObjectNode)nodos.get(id).get("data")).set("parent", parent != null ? parent : NullNode.getInstance());

        return json.toString();
    }

    private void agregarOEliminarRelacionEntreNodoYGrupo(String id, JsonNode parent, Session session){
        Relacion relacion = new Relacion();
        relacion.setIdNodo(Integer.parseInt(id));

        if(parent == null){//Elimino una relación entre grupo y nodo
            EndPointHandler.eliminarRelacionNodoGrupo(relacion);
            return;
        }

        //Creo una relación enrte grupo y nodo
        if(esIdSecuencial(parent.asText())){
            System.out.println("Cliente nuevo servidor nuevo");
            relacion.setIdGrupoPadre(Integer.parseInt(parent.asText()));
            EndPointHandler.agregarRelacionNodoGrupo(relacion);
            return;
        }

        boolean existeGrupoPorIdProvicional = EndPointHandler.existeGrupoPorIdProvicional(parent.asText(), session);

        if(existeGrupoPorIdProvicional){
            System.out.println("Cliente viejo servidor viejo");
            System.out.println("ERROR GRAVISIMO. Esto no debería suceder. En el servidor hay id secuencial y significa que no la inserción del grupo no se ha terminado.");
        }else{
            System.out.println("Cliente viejo servidor nuevo");
            JsonNode grupo = EndPointHandler.darGrupoPorIdProvicional(parent.asText(), session);
            String idGrupoString = grupo.get("data").get("id").asText();
            relacion.setIdGrupoPadre(Integer.parseInt(idGrupoString));
            EndPointHandler.agregarRelacionNodoGrupo(relacion);
        }
    }

    private boolean esIdSecuencial(String id){
        try{
            Integer.parseInt(id);
            return true;
        }catch (NumberFormatException ignored){
            return false;
        }
    }

    private String eliminarElemento(JsonNode json, Session session) {
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();
        JsonNode elemento = json.get("elemento");
        String id = elemento.get("data").get("id").asText();

        nodos.remove(id);

        switch (json.get("tipo").asText()){
            case "Grupo":
                eliminarGrupo(elemento);
                break;
            case "Edge grupo":
                eliminarRelacionGrupoAGrupo(elemento);
                break;
            case "Edge nodo":
                eliminarRelacionNodoANodo(elemento);
                break;
            default: break;
        }

        return json.toString();
    }

    private void eliminarGrupo(JsonNode elemento){
        String idGrupo = elemento.get("data").get("id").asText();
        EndPointHandler.eliminarGrupo(Integer.parseInt(idGrupo));
    }

    private void eliminarRelacionGrupoAGrupo(JsonNode elemento){
        String idPadre = elemento.get("data").get("source").asText();
        String id = elemento.get("data").get("target").asText();
        Relacion relacion = new Relacion();
        relacion.setIdGrupo(Integer.parseInt(id));
        relacion.setIdGrupoPadre(Integer.parseInt(idPadre));
        EndPointHandler.eliminarRelacionGrupoAGrupo(relacion);
    }

    private void eliminarRelacionNodoANodo(JsonNode elemento){
        String idPadreString = elemento.get("data").get("source").asText();
        String idString = elemento.get("data").get("target").asText();
        int idPadre = Integer.parseInt(idPadreString);
        int id = Integer.parseInt(idString);
        EndPointHandler.eliminarRelacionNodoANodo(id, idPadre);
    }

    private void enviarNodosACliente(Session session) {
        Sala sala = EndPointHandler.darSala(EndPointHandler.extraerIdSala(session));
        HashMap<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("accion", "Nodos");
        jsonMap.put("nodos", sala.getNodos().values());
        jsonMap.put("solicitantes", darSolicitantesPorSala(sala));

        try {
            String jsonString = new ObjectMapper().writeValueAsString(jsonMap);
            difundirA(jsonString, session);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private List<ObjectNode> darSolicitantesPorSala(Sala sala){
        return sala.getClientes().values().stream().map(sesionCliente -> {
            ObjectNode solicitante = new ObjectMapper().createObjectNode();
            solicitante.set("nombre", new TextNode(sesionCliente.getNombre()));
            solicitante.set("email", new TextNode(sesionCliente.getEmail()));
            solicitante.set("solicitandoOrganizacion", sesionCliente.getSolicitandoOrganizacion() ? BooleanNode.TRUE : BooleanNode.FALSE);
            return solicitante;
        }).collect(Collectors.toList());
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

    private String bloquearNodos(JsonNode json, Session sesion) {
        int idSala = EndPointHandler.extraerIdSala(sesion);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodosActuales = sala.getNodos();
        JsonNode nodos = json.get("nodos");
        for(final JsonNode nodo : nodos){
            JsonNode nodoACambiar = nodosActuales.get(nodo.get("id").asText());
            ((ObjectNode)nodoACambiar.get("data")).set("bloqueado", BooleanNode.TRUE);
        }
        return json.toString();
    }

    private String desbloquearNodos(JsonNode json, Session sesion) {
        int idSala = EndPointHandler.extraerIdSala(sesion);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodosActuales = sala.getNodos();
        JsonNode nodos = json.get("nodos");
        for(final JsonNode nodo : nodos){
            JsonNode nodoACambiar = nodosActuales.get(nodo.get("id").asText());
            ((ObjectNode)nodoACambiar.get("data")).set("bloqueado", BooleanNode.FALSE);
        }
        return json.toString();
    }

    private String moverNodo(JsonNode json, Session session) {
        EndPointHandler.actualizarPosicionNodo(json.get("elemento"), session);
        return json.toString();
    }

    private String moverNodoPadre(JsonNode json, Session session) {
        JsonNode nodosHijos = json.get("nodosHijos");
        for(JsonNode nodo : nodosHijos){
            EndPointHandler.actualizarPosicionNodo(nodo, session);
        }
        ((ObjectNode)json).set("accion", new TextNode("Mover"));
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

        if(solicitantes != solicitantesTotales)return json.toString();

        //Envio reinicio de solicitudes a todos los nodos menos el actual.
        ObjectNode mensajeReiniciarSolicitudes = new ObjectMapper().createObjectNode();
        mensajeReiniciarSolicitudes.set("accion", new TextNode("Reiniciar solicitudes"));
        mensajeReiniciarSolicitudes.set("nodos", EndPointHandler.darPosicionesIniciales(session));
        difundirATodos(session, mensajeReiniciarSolicitudes.toString());

        //Cambiar las solicitudes actuales.
        sala.getClientes()
            .values()
            .stream()
            .forEach(sesionCliente -> sesionCliente.setSolicitandoOrganizacion(false));

        return "{}";
    }

    private String cambiarNombre(JsonNode json, Session session){
        int idSala = EndPointHandler.extraerIdSala(session);
        Sala sala = EndPointHandler.darSala(idSala);
        Map<String, JsonNode> nodos = sala.getNodos();

        String id = json.get("grupo").get("data").get("id").asText();
        String nuevoNombre = json.get("grupo").get("data").get("nombre").asText();

        ((ObjectNode) nodos.get(id).get("data")).replace("nombre", new TextNode(nuevoNombre));

        //TODO: Persistir los cambios en la DB.

        return json.toString();
    }

    private void difundirATodosMenosA(Session sesion, String mensaje) {
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

    private void difundirATodos(Session sesion, String mensaje){
        int idSala = EndPointHandler.extraerIdSala(sesion);
        Map<String, SesionCliente> endPoints = EndPointHandler.darSesionesPorSala(idSala);

        endPoints.forEach((key, value) -> difundirA(mensaje, value.getSesion()));
    }
}