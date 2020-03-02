package ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import entity.*;
import org.eclipse.jetty.websocket.api.Session;
import usecase.GrupoUseCase;
import usecase.NodoUseCase;
import usecase.RelacionUseCase;
import util.SingletonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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


        synchronized (SingletonUtils.lock){
            if(sala.getClientes().size() != 1) return;// Es el primer cliente en conectarse
            List<JsonNode> gruposYConexiones = grupoUseCase.darGrupos(idProblematica);
            List<JsonNode> nodosYConexiones = nodoUseCase.darNodosPorProblematica(idProblematica);

            nodosYConexiones.addAll(gruposYConexiones);
            sala.cambiarNodos(nodosYConexiones);
        }
    }

    static void eliminarCliente(Session sesion){
        int idSala = extraerIdSala(sesion);
        Sala sala = salasActivas.get(idSala);
        sala.eliminarCliente(sesion);
        if(sala.getClientes().size() != 0)return;
        synchronized (SingletonUtils.lock){
            salasActivas.remove(idSala);
        }
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

    public static Grupo agregarGrupo(int idProblematica, Grupo grupo) {
        return grupoUseCase.agregarGrupo(idProblematica, grupo);
    }

    public static void agregarRelacionNodoGrupo(Relacion relacion) {
        relacionUseCase.conectarNodoYGrupo(relacion);
    }

    public static void eliminarRelacionNodoGrupo(Relacion relacion) {
        relacionUseCase.desconectarNodoYGrupo(relacion);
    }

    public static void eliminarGrupo(int idGrupo) {
        grupoUseCase.eliminarGrupo(idGrupo);
    }

    public static void agregarRelacionNodoANodo(Relacion relacion) {
        relacionUseCase.conectarNodos(relacion);
    }

    public static void agregarRelacionGrupoAGrupo(Relacion relacion) {
        relacionUseCase.conectarGrupos(relacion);
    }

    public static void eliminarRelacionGrupoAGrupo(Relacion relacion) {
        relacionUseCase.desconectarGrupos(relacion);
    }

    public static void eliminarRelacionNodoANodo(int id, int idPadre) {
        relacionUseCase.desconectarNodos(id, idPadre);
    }

    public static boolean existeGrupoPorIdProvicional(String idProvicional, Session session) {
        Sala sala = darSala(extraerIdSala(session));
        JsonNode jsonNode = sala.getNodos().get(idProvicional);
        return jsonNode != null;
    }

    public static JsonNode darGrupoPorIdProvicional(String idProvicional, Session session) {
        Sala sala = darSala(extraerIdSala(session));
        Map<String, JsonNode> nodos = sala.getNodos();
        Set<Map.Entry<String, JsonNode>> set = nodos.entrySet();
        for (Map.Entry<String, JsonNode> entry: set) {
            JsonNode grupo = entry.getValue();
            JsonNode idAntiguo = grupo.get("data").get("idAntiguo");
            if(idAntiguo != null && idAntiguo.asText().equals(idProvicional)){ return grupo; }
        }
        return null;
    }
}