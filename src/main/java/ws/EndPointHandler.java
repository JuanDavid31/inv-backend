package ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import entity.*;
import org.eclipse.jetty.websocket.api.Session;
import usecase.GrupoUseCase;
import usecase.NodoUseCase;
import util.SingletonUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EndPointHandler {

    public static GrupoUseCase grupoUseCase;
    public static NodoUseCase nodoUseCase;

    private static Map<Integer, Sala> salasActivas = new ConcurrentHashMap<>();

    static void agregarCliente(Session sesion) {
        int idProblematica = extraerIdSala(sesion);
        //Si no existe la sala entonces la crea.
        salasActivas.computeIfAbsent(idProblematica, k -> new Sala(new HashMap<String, SesionCliente>(), new HashMap<String, JsonNode>()));
        Sala sala = salasActivas.get(idProblematica);
        sala.agregarSesion(sesion);

        if(sala.getClientes().size() != 1) return; // Es el primer cliente en conectarse
        synchronized (SingletonUtils.lock){
            List<JsonNode> grupos = grupoUseCase.darGrupos(idProblematica);
            List<JsonNode> nodos = nodoUseCase.darNodosPorProblematica(idProblematica);
            nodos.addAll(grupos);
            sala.cambiarNodos(nodos);
        }
    }

    static void eliminarCliente(Session sesion){
        int idSala = extraerIdSala(sesion);
        Sala sala = salasActivas.get(idSala);
        sala.eliminarCliente(sesion);
        if(sala.getClientes().size() != 0)return;
        synchronized (SingletonUtils.lock){
            agregarNuevosGrupos(sala, idSala);
            eliminarGruposNuevosDeGruposActuales(sala);
            mapearConexionesActuales(sala);
            actualizarNodosActuales(sala);
            eliminarGrupos(sala, idSala);
            salasActivas.remove(idSala);
        }
    }

    /**
     * La adición se divide en 2, primero se agregan los grupos sin padre y despues los restantes.
     * El orden asegura la integridad de la DB.
     * @param sala
     */
    private static void agregarNuevosGrupos(Sala sala, int idProblematica) {
        Map<String, JsonNode> gruposAgregados = sala.getGruposAgregados();
        mapearNuevasConexiones(gruposAgregados, sala.getNodos());
        agregarGruposSinPadre(gruposAgregados, idProblematica);
        agregarGruposConPadre(gruposAgregados, idProblematica);
    }

    private static void mapearNuevasConexiones(Map<String, JsonNode> grupos, final Map<String, JsonNode> gruposActuales) {
        grupos.values()
            .stream()
            .filter(grupo -> grupo.get("data").get("source") != null)
            .forEach(consumerWrapper(conexion -> {
                //String idString = conexion.get("data").get("id").asText();
                String sourceString = conexion.get("data").get("source").asText();
                String targetString = conexion.get("data").get("target").asText();

                if(grupos.get(targetString) != null){
                    //Si el grupo es nuevo entonces su id seran letras autogeneradas
                    ((ObjectNode)grupos.get(targetString).get("data")).replace("parent", new TextNode(sourceString));
                }else{//Es viejo. Su id sera un número.
                    ((ObjectNode)gruposActuales.get(targetString).get("data")).replace("parent", new TextNode(sourceString));
                }
            }));
    }

    /**
     * Grupos con padre en null seran mapeados a Grupo(s), agregados a la db y con el nuevo id que se le fue asignado
     * actualizara el valor del hashmap grupos por un nodo simple con el nuevo id.
     * @param grupos
     * @param idProblematica
     */
    private static void agregarGruposSinPadre(Map<String, JsonNode> grupos, int idProblematica){
        grupos.values()
            .stream()
            .filter(grupo -> grupo.get("data").get("parent") == null && grupo.get("data").get("source") == null)
            .forEach(consumerWrapper(grupo -> {
                String idProvicional = grupo.get("data").get("id").asText();
                String nombreGrupo = grupo.get("data").get("nombre").asText();

                Grupo nuevoGrupo = grupoUseCase.agregarGrupo(idProblematica, new Grupo(0, nombreGrupo));

                grupos.replace(idProvicional, new IntNode(nuevoGrupo.getId()));
            }));
    }

    /**
     * A cada grupo iterado se le actualizara el padre con el id previamente asignado por la db, paso seguido
     * se añadiran a la db.
     * @param grupos
     * @param idProblematica
     */
    private static void agregarGruposConPadre(Map<String, JsonNode> grupos, int idProblematica){
        grupos.values()
            .stream()
            .filter(grupo -> grupo.get("data") != null)//En este punto los nodos sin padre fueron reemplazados por {}.
            .filter(grupo -> grupo.get("data").get("source") == null)//Que no sea un edge.
            .forEach(consumerWrapper(grupo -> {
                String idProvicional = grupo.get("data").get("id").asText();
                String nombreGrupo = grupo.get("data").get("nombre").asText();

                String stringIdPadre = grupo.get("data").get("parent").asText();

                if(grupos.containsKey(stringIdPadre)){
                    ((ObjectNode)grupo.get("data")).replace("parent", grupos.get(stringIdPadre));
                }else{
                    ((ObjectNode)grupo.get("data")).replace("parent", new IntNode(Integer.parseInt(stringIdPadre)));
                }

                int idPadre = grupo.get("data").get("parent").asInt();

                Grupo nuevoGrupo = grupoUseCase.agregarGrupo(idProblematica, new Grupo(0, nombreGrupo, idPadre));

                grupos.replace(idProvicional, new IntNode(nuevoGrupo.getId()));
            }));
    }

    private static void eliminarGruposNuevosDeGruposActuales(Sala sala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        sala.getGruposAgregados()
            .keySet()
            .forEach(nodos::remove);
    }

    private static void mapearConexionesActuales(Sala sala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        nodos.values()
            .stream()
            .filter(nodo -> nodo.get("data").get("source") != null)
            .forEach(consumerWrapper(conexion -> {
                String sourceString = conexion.get("data").get("source").asText();
                String targetString = conexion.get("data").get("target").asText();

                ((ObjectNode)nodos.get(targetString).get("data")).replace("parent", new IntNode(Integer.parseInt(sourceString)));
            }));
    }

    private static void eliminarGrupos(Sala sala, int idSala) {
        Map<String, JsonNode> gruposEliminados = sala.getGruposEliminados();

        //Eliminar conexiones
        List<Integer> idsTargets = gruposEliminados.values()
                .stream()
                .filter(nodo -> nodo.get("data") != null)
                .filter(nodo -> nodo.get("data").get("source") != null)
                .map(nodo -> Integer.parseInt(nodo.get("data").get("target").asText()))
                .collect(Collectors.toList());

        grupoUseCase.eliminarConexiones(idsTargets);

        List<Integer> idsGrupos = gruposEliminados.entrySet()
                .stream()
                .map(entry -> Integer.parseInt(entry.getKey()))
                .collect(Collectors.toList());

        grupoUseCase.eliminarGrupos(idsGrupos, idSala);
    }

    private static void actualizarNodosActuales(Sala sala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        Map<String, JsonNode> gruposAgregados = sala.getGruposAgregados();
        nodos.values()
            .stream()
            .filter(nodo -> nodo.get("data").get("source") == null)
            .forEach(consumerWrapper(nodo -> {
                boolean esGrupo = nodo.get("data").get("esGrupo") != null;
                if(esGrupo){
                    int id = nodo.get("data").get("id").asInt();
                    String nombre = nodo.get("data").get("nombre").asText();

                    JsonNode nodoIdPadre = nodo.get("data").get("parent");
                    if(nodoIdPadre != null && !nodoIdPadre.isNull()){
                        String nuevoIdPadreString = nodoIdPadre.asText();
                        int nuevoIdPadre = gruposAgregados.containsKey(nodoIdPadre.asText()) ?
                                gruposAgregados.get(nuevoIdPadreString).asInt() :
                                Integer.parseInt(nuevoIdPadreString);

                        grupoUseCase.actualizarNombreYPadreGrupo(new Grupo(id, nombre, nuevoIdPadre));
                    }else{
                        grupoUseCase.actualizarNombreYPadreGrupo(new Grupo(id, nombre));
                    }
                }else{
                    int id = nodo.get("data").get("id").asInt();
                    String idGrupo = nodo.get("data").get("parent").asText();

                    JsonNode grupo = gruposAgregados.get(idGrupo);
                    if(grupo != null){
                        ((ObjectNode)nodo.get("data")).replace("parent", grupo);
                        int nuevoIdGrupo = nodo.get("data").get("parent").asInt();

                        nodoUseCase.actualizarGrupoNodo(new Nodo(id, nuevoIdGrupo));
                    }else{
                        Integer nuevoIdPadre = idGrupo.equals("null") ? null: Integer.parseInt(idGrupo);
                        nodoUseCase.actualizarGrupoNodo(new Nodo(id, nuevoIdPadre));
                    }
                }
            }));
    }

    /**
     * Retorna el map de sesiones cliente de la sala dado el id de la problematica o id de la sala.
     * @param idProblematica
     * @return
     */
    static Map<String, SesionCliente> darSesionesPorSala(int idProblematica) {
        return salasActivas.get(idProblematica).getClientes();
    }

    static int extraerIdSala(Session sesion) {
        //idSala -> "[1]" si se usa esta ruta ws://localhost:8080/colaboracion?idProblematica=1
        String idSala = sesion.getUpgradeRequest().getParameterMap().get("idProblematica").get(0);
        return Integer.parseInt(idSala);
    }

    static void actualizarPosicionNodo(JsonNode json, Session sesion) {
        Sala sala = salasActivas.get(extraerIdSala(sesion));
        sala.actualizarNodo(json);
    }

    static void guardarPosicionesIniciales(JsonNode nodos, Session sesion){
        Sala sala = salasActivas.get(extraerIdSala(sesion));
        sala.setPosicionesIniciales(nodos);
    }

    static JsonNode darPosicionesIniciales(Session sesion){
        Sala sala = salasActivas.get(extraerIdSala(sesion));
        return sala.getPosicionesIniciales();
    }

    public static Sala darSala(int idSala) {
        return salasActivas.get(idSala);
    }

    private static <T, E extends Exception> Consumer<T> consumerWrapper(Consumer<T> consumer) {

        return i -> {
            try {
                consumer.accept(i);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }
}