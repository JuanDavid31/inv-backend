package ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import entity.Grupo;
import entity.Nodo;
import entity.Sala;
import entity.SesionCliente;
import org.eclipse.jetty.websocket.api.Session;
import usecase.GrupoUseCase;
import usecase.NodoUseCase;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EndPointHandler {

    public static GrupoUseCase grupoUseCase;
    public static NodoUseCase nodoUseCase;

    private static EndPointHandler instance;
    private static Map<Integer, Sala> salasActivas = new ConcurrentHashMap<>();//TODO: Esta variable debe ser ultra thread safe

    static void agregarCliente(Session sesion) {
        int idProblematica = extraerIdSala(sesion);
        //Si no existe la sala entonces la crea.
        salasActivas.computeIfAbsent(idProblematica, k -> new Sala(new HashMap<String, SesionCliente>(), new HashMap<String, JsonNode>()));
        Sala sala = salasActivas.get(idProblematica);
        sala.agregarSesion(sesion);

        if(sala.getClientes().size() == 1){ // Es el primer cliente en conectarse
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
        if(sala.getClientes().size() == 0){
            agregarNuevosGrupos(sala, idSala);
            eliminarGruposNuevosDeGruposActuales(sala);
            eliminarGrupos(sala, idSala);
            actualizarNodosActuales(sala, idSala);
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
        mapearNuevasConexiones(gruposAgregados);
        agregarGruposSinPadre(gruposAgregados, idProblematica);
        agregarGruposConPadre(gruposAgregados, idProblematica);
    }

    private static void mapearNuevasConexiones(Map<String, JsonNode> grupos) {
        grupos.values()
            .stream()
            .filter(grupo -> grupo.get("data").get("source") != null)
            .forEach(conexion -> {
                String idString = conexion.get("data").get("id").asText();
                String sourceString = conexion.get("data").get("source").asText();
                String targetString = conexion.get("data").get("target").asText();
                //TODO: Mucho por hacer
                ((ObjectNode)grupos.get(targetString).get("data")).replace("parent", new IntNode(Integer.parseInt(sourceString)));

                grupos.remove(idString);
            });
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
            .forEach(grupo -> {
                String idProvicional = grupo.get("data").get("id").asText();
                String nombreGrupo = grupo.get("data").get("nombre").asText();

                Grupo nuevoGrupo = grupoUseCase.agregarGrupo(idProblematica, new Grupo(nombreGrupo));

                grupos.replace(idProvicional, new IntNode(nuevoGrupo.id));

            });
    }

    /**
     * A cada grupo iterado se le actualizara el padre con el id previamente asignado por la db, paso seguido
     * se añadiran a la db.
     * @param grupos
     * @param idProblematica
     */
    private static void agregarGruposConPadre(Map<String, JsonNode> grupos, int idProblematica){
        grupos.values()
            .stream()//En este punto los nodos reemplazados no tendran data.
            .filter(grupo -> grupo.get("data") != null)
            .forEach(grupo -> {
                String idProvicional = grupo.get("data").get("id").asText();
                String nombreGrupo = grupo.get("data").get("nombre").asText();
                int idPadre = grupo.get("data").get("parent").asInt();
                ((ObjectNode)grupo.get("data")).replace("parent", grupos.get(idPadre));
                idPadre = grupo.get("data").get("parent").asInt();

                Grupo nuevoGrupo = grupoUseCase.agregarGrupo(idProblematica, new Grupo(nombreGrupo, idPadre));

                grupos.replace(idProvicional, new IntNode(nuevoGrupo.id));
            });
    }

    private static void eliminarGruposNuevosDeGruposActuales(Sala sala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        sala.getGruposAgregados()
            .keySet()
            .forEach(nodos::remove);
    }

    private static void eliminarGrupos(Sala sala, int idSala) {
        Map<String, JsonNode> gruposEliminados = sala.getGruposEliminados();
        List idsGrupos = gruposEliminados.entrySet()
                .stream()
                .map(entry -> Integer.parseInt(entry.getKey()))
                .collect(Collectors.toList());
        grupoUseCase.eliminarGrupos(idsGrupos, idSala);
    }

    private static void actualizarNodosActuales(Sala sala, int idSala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        nodos.values()
            .forEach(nodo -> {
                boolean esGrupo = nodo.get("data").get("esGrupo") != null;
                if(esGrupo){
                    int id = Integer.parseInt(nodo.get("data").get("id").asText());
                    String nombre = nodo.get("data").get("nombre").asText();
                    //nodo.get("data").get("parent").as; TODO: Pendiente
                    grupoUseCase.actualizarGrupo(id, new Grupo(nombre));
                }else{
                    int id = Integer.parseInt(nodo.get("data").get("id").asText());
                    String idGrupo = nodo.get("data").get("parent").asText();

                    Map<String, JsonNode> gruposAgregados = sala.getGruposAgregados();
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
            });
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

    public static Sala darSala(int idSala) {
        return salasActivas.get(idSala);
    }

    public static class EndPointHandlerBuilder{

        public static void build(){
            instance = new EndPointHandler();
        }
    }

    public EndPointHandler getInstance(){
        return instance;
    }
}