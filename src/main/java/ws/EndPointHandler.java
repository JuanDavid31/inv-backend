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
            actualizarNombresAGruposActuales(sala);
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
        agregarRelacionesGrupoNodo(sala.getRelacionesNodoAGrupoAgregadas(), gruposAgregados);
        agregarRelacionesGrupoAGrupo(gruposAgregados);
        agregarRelacionesNodoANodo(gruposAgregados);
    }

    /**
     * Grupos con padre en null seran mapeados a Grupo(s), agregados a la db y con el nuevo id que se le fue asignado
     * actualizara el valor del hashmap grupos por un nodo simple con el nuevo id.
     * @param gruposNuevos
     * @param idProblematica
     */
    private static void agregarGrupos(Map<String, JsonNode> gruposNuevos, int idProblematica){
        System.out.println("agregarGrupos()");
        gruposNuevos.values()
                .stream()
                .filter(grupo -> grupo.get("data").get("esGrupo") != null)
                .forEach(System.out::println);

        gruposNuevos.values()
            .stream()
            .filter(grupo -> grupo.get("data").get("esGrupo") != null)
            .forEach(consumerWrapper(grupo -> {
                
                String idProvicional = grupo.get("data").get("id").asText();
                String nombreGrupo = grupo.get("data").get("nombre").asText();

                Grupo nuevoGrupo = grupoUseCase.agregarGrupo(idProblematica, new Grupo(0, nombreGrupo));
                
                System.out.println("Grupo agregado -> " + nuevoGrupo.toString());

                gruposNuevos.replace(idProvicional, new IntNode(nuevoGrupo.getId()));
            }));
    }

    public final static int ID_GRUPO_INICIAL = 10000;

    private static void agregarRelacionesGrupoNodo(Map<String, JsonNode> relacionesNuevas,
                                                   Map<String, JsonNode> gruposAgregados){
        System.out.println("agregarRelacionesGrupoNodo()");
        relacionesNuevas.forEach((s, jsonNode) -> System.out.println(jsonNode));
        relacionesNuevas.values()
                .stream()
                .filter(elemento -> !elemento.isInt())
                .filter(grupo -> grupo.get("data").get("source") != null)
                .filter(grupo -> grupo.get("data").get("source").asInt() >= ID_GRUPO_INICIAL && grupo.get("data").get("target").asInt() < ID_GRUPO_INICIAL)
                .forEach(consumerWrapper(conexion -> {
                    String sourceString = conexion.get("data").get("source").asText();
                    String targetString = conexion.get("data").get("target").asText();

                    int idPadre;
                    if(gruposAgregados.get(sourceString) != null){ //El grupo es nuevo.
                        idPadre = gruposAgregados.get(sourceString).asInt();
                    }else{//El grupo es viejo.
                        idPadre = Integer.parseInt(sourceString);
                    }

                    int id = Integer.parseInt(targetString);

                    Relacion relacion = new Relacion();

                    relacion.setIdGrupoPadre(idPadre);
                    relacion.setIdNodo(id);


                    Boolean exito = relacionUseCase.conectarNodoYGrupo(relacion);
                    System.out.println(exito + " " + relacion.toString());
                }));
    }

    private static void agregarRelacionesGrupoAGrupo(Map<String, JsonNode> gruposAgregados){
        System.out.println("agregarRelacionesGrupoAGrupo()");
        gruposAgregados.values()
                .stream()
                .filter(elemento -> !elemento.isInt())
                .filter(grupo -> grupo.get("data").get("source") != null)
                .filter(grupo -> grupo.get("data").get("source").asInt() >= ID_GRUPO_INICIAL && grupo.get("data").get("target").asInt() >= ID_GRUPO_INICIAL)
                .forEach(System.out::println);

        gruposAgregados.values()
                .stream()
                .filter(elemento -> !elemento.isInt())
                .filter(grupo -> grupo.get("data").get("source") != null)
                .filter(grupo -> grupo.get("data").get("source").asInt() >= ID_GRUPO_INICIAL && grupo.get("data").get("target").asInt() >= ID_GRUPO_INICIAL)
                .forEach(consumerWrapper(conexion -> {
                    String sourceString = conexion.get("data").get("source").asText();
                    String targetString = conexion.get("data").get("target").asText();

                    int idPadre;

                    if(gruposAgregados.get(sourceString) != null){ //Grupo nuevo
                        idPadre = gruposAgregados.get(sourceString).asInt();
                    }else{//Grupo viejo
                        idPadre = Integer.parseInt(sourceString);
                    }

                    int id;

                    if(gruposAgregados.get(targetString) != null){ //Grupo nuevo
                        id = gruposAgregados.get(targetString).asInt();
                    }else{//Grupo viejo
                        id = Integer.parseInt(targetString);
                    }

                    Relacion relacion = new Relacion();

                    relacion.setIdGrupoPadre(idPadre);
                    relacion.setIdGrupo(id);
                    relacion.setFase(2);

                    boolean exito = relacionUseCase.conectarGrupos(relacion);
                    System.out.println(exito + " " + relacion.toString());
                }));
    }

    private static void agregarRelacionesNodoANodo(Map<String, JsonNode> gruposAgregados){
        System.out.println("agregarRElaconesNodoANodo()");
        gruposAgregados.values()
                .stream()
                .filter(elemento -> !elemento.isInt())
                .filter(grupo -> grupo.get("data").get("source") != null)
                .filter(grupo -> grupo.get("data").get("source").asInt() < ID_GRUPO_INICIAL && grupo.get("data").get("target").asInt() < ID_GRUPO_INICIAL)
                .forEach(System.out::println);

        gruposAgregados.values()
                .stream()
                .filter(elemento -> !elemento.isInt())
                .filter(grupo -> grupo.get("data").get("source") != null)
                .filter(grupo -> grupo.get("data").get("source").asInt() < ID_GRUPO_INICIAL && grupo.get("data").get("target").asInt() < ID_GRUPO_INICIAL)
                .forEach(consumerWrapper(conexion -> {
                    int idPadre = conexion.get("data").get("source").asInt();
                    int id = conexion.get("data").get("target").asInt();


                    Relacion relacion = new Relacion();

                    relacion.setIdNodoPadre(idPadre);
                    relacion.setIdNodo(id);
                    relacion.setFase(2);

                    boolean exito = relacionUseCase.conectarNodos(relacion);
                    System.out.println(exito + " " + relacion.toString());
                }));
    }

    private static void eliminarGruposNuevosDeGruposActuales(Sala sala) {
        Map<String, JsonNode> nodos = sala.getNodos();
        sala.getGruposAgregados()
            .keySet()
            .forEach(nodos::remove);
    }

    private static void actualizarNombresAGruposActuales(Sala sala) {
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
        eliminarConexiones(sala.getRelacionesNodoAGrupoEliminadas());
        eliminarConexiones(gruposEliminados);

        List<Integer> idsGrupos = gruposEliminados.entrySet()
                .stream()
                .filter(entry -> entry.getValue().get("data") != null)
                .filter(entry -> entry.getValue().get("data").get("esGrupo") != null)
                .map(entry -> Integer.parseInt(entry.getKey()))
                .collect(Collectors.toList());

        System.out.println("Grupos a eliminar " + idsGrupos);
        grupoUseCase.eliminarGrupos(idsGrupos, idSala);
    }

    private static void eliminarConexiones(Map<String, JsonNode> elementos){
        System.out.println("eliminarConexiones()");
        elementos.values()
                .stream()
                .filter(nodo -> nodo.get("data") != null)
                .filter(nodo -> nodo.get("data").get("source") != null)
                .forEach(System.out::println);
        elementos.values()
                .stream()
                .filter(nodo -> nodo.get("data") != null)
                .filter(nodo -> nodo.get("data").get("source") != null)
                .forEach(nodo -> {
                    int id = Integer.parseInt(nodo.get("data").get("target").asText()); //Estos ids son secuenciales, por tanto no hay problema.
                    int idPadre = Integer.parseInt(nodo.get("data").get("source").asText()); //Pues estos son nodos viejos

                    Relacion relacion = new Relacion();

                    if(idPadre >= ID_GRUPO_INICIAL){
                        relacion.setIdGrupoPadre(idPadre);
                    }else{
                        relacion.setIdNodoPadre(idPadre);
                    }

                    if(id >= ID_GRUPO_INICIAL){
                        relacion.setIdGrupo(id);
                    }else{
                        relacion.setIdNodo(id);
                    }

                    if(idPadre >= ID_GRUPO_INICIAL){
                        if(id >= ID_GRUPO_INICIAL){
                            boolean exito = relacionUseCase.desconectarGrupos(relacion);
                            System.out.println("Desconectando grupos -> " + exito + " " + relacion.toString());
                        }else{
                            Boolean exito = relacionUseCase.desconectarNodoYGrupo(relacion);
                            System.out.println("Desconectando NodoYGrupo-> " + exito + " " + relacion.toString());
                        }
                    }else if(id <= ID_GRUPO_INICIAL){
                        boolean exito = relacionUseCase.desconectarNodos(relacion.getIdNodo(), relacion.getIdNodoPadre());
                        System.out.println("Desconectando nodos -> " + exito + " " + relacion.toString());
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