package ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;

import entity.*;
import org.eclipse.jetty.websocket.api.Session;
import usecase.GrupoUseCase;
import usecase.NodoUseCase;
import usecase.RelacionUseCase;
import util.SingletonUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EndPointHandler {

    public static GrupoUseCase grupoUseCase;
    public static NodoUseCase nodoUseCase;
    public static RelacionUseCase relacionUseCase;

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
        agregarGrupos(gruposAgregados, idProblematica);
        agregarRelacionesNuevas(gruposAgregados);
    }

    /**
     * Grupos con padre en null seran mapeados a Grupo(s), agregados a la db y con el nuevo id que se le fue asignado
     * actualizara el valor del hashmap grupos por un nodo simple con el nuevo id.
     * @param grupos
     * @param idProblematica
     */
    private static void agregarGrupos(Map<String, JsonNode> grupos, int idProblematica){
        grupos.values()
            .stream()
            .filter(grupo -> grupo.get("data").get("esGrupo") != null)
            .forEach(consumerWrapper(grupo -> {
                String idProvicional = grupo.get("data").get("id").asText();
                String nombreGrupo = grupo.get("data").get("nombre").asText();

                Grupo nuevoGrupo = grupoUseCase.agregarGrupo(idProblematica, new Grupo(0, nombreGrupo));

                grupos.replace(idProvicional, new IntNode(nuevoGrupo.getId()));
            }));
    }

    private static void agregarRelacionesNuevas(Map<String, JsonNode> grupos){
        grupos.values()
                .stream()
                .filter(grupo -> grupo.get("data").get("source") != null)
                .forEach(consumerWrapper(conexion -> {
                    String sourceString = conexion.get("data").get("source").asText();
                    String targetString = conexion.get("data").get("target").asText();

                    int idPadre = grupos.get(sourceString).asInt();
                    int id = grupos.get(targetString).asInt();

                    Relacion relacion = new Relacion();

                    if(id >= 10000 && idPadre >= 10000){ //Agrego edge grupos
                        relacion.setIdGrupo(id);
                        relacion.setIdGrupoPadre(idPadre);

                        relacionUseCase.conectarGrupos(relacion);
                    }else{
                        relacion.setIdNodo(id);
                        relacion.setIdNodoPadre(idPadre);

                        relacionUseCase.conectarNodoYGrupo(relacion);
                    }
                }));
    }

    private static void eliminarGruposNuevosDeGruposActuales(Sala sala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        sala.getGruposAgregados()
            .keySet()
            .forEach(nodos::remove);
    }

    private static void actualizarNodosActuales(Sala sala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        nodos.values()
            .stream()
            .filter(nodo -> nodo.get("data").get("source") == null)
            .filter(nodo -> nodo.get("data").get("esGrupo") != null)
            .forEach(consumerWrapper(nodo -> {
                int id = nodo.get("data").get("id").asInt();
                String nombre = nodo.get("data").get("nombre").asText();

                grupoUseCase.actualizarNombre(new Grupo(id, nombre));
            }));
    }

    private static void eliminarGrupos(Sala sala, int idSala) {
        Map<String, JsonNode> gruposEliminados = sala.getGruposEliminados();

        gruposEliminados.values()
                .stream()
                .filter(nodo -> nodo.get("data") != null)
                .filter(nodo -> nodo.get("data").get("source") != null)
                .forEach(nodo -> {
                    int id = Integer.parseInt(nodo.get("data").get("target").asText());
                    int idPadre = Integer.parseInt(nodo.get("data").get("source").asText());

                    Relacion relacion = new Relacion();

                    if(id >= 10000 && idPadre >= 10000){ //Agrego edge grupos
                        relacion.setIdGrupo(id);
                        relacion.setIdGrupoPadre(idPadre);

                        relacionUseCase.desconectarGrupos(relacion);
                    }else{
                        relacion.setIdNodo(id);
                        relacion.setIdNodoPadre(idPadre);

                        relacionUseCase.desconectarNodoYGrupo(relacion);
                    }
                });

        List<Integer> idsGrupos = gruposEliminados.entrySet()
                .stream()
                .map(entry -> Integer.parseInt(entry.getKey()))
                .collect(Collectors.toList());

        grupoUseCase.eliminarGrupos(idsGrupos, idSala);
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