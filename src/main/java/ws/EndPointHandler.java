package ws;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EndPointHandler {

    private static EndPointHandler instance;
    public static Map<Long, Map<String, Session>> gruposActivos = new ConcurrentHashMap<>();//TODO: Esta variable debe ser ultra thread safe

    public static void agregarCliente(Session sesion) {
        Long idProblematica = darIdProblematica(sesion);
        gruposActivos.computeIfAbsent(idProblematica, k -> new HashMap<String, Session>())
            .put(String.valueOf(sesion.hashCode()), sesion);
    }

    private static Long darIdProblematica(Session sesion) {
        String idProblematica = sesion.getUpgradeRequest().getParameterMap().get("idProblematica").get(0);
        System.out.println(idProblematica);//Imprime [1] si se usa esta ruta ws://localhost:8080/colaboracion?idProblematica=1
        return Long.parseLong(idProblematica);
    }

    public static void eliminarCliente(Session sesion){
        Long idProblematica = darIdProblematica(sesion);
        Map grupo = gruposActivos.get(idProblematica);
        grupo.remove(String.valueOf(sesion.hashCode()), sesion);
        if(grupo.size() == 0){
            gruposActivos.remove(idProblematica);
        }
    }

    public static Map<String, Session> darSesionesPorGrupo(Session sesion) {
        Long idProblematica = darIdProblematica(sesion);
        return gruposActivos.get(idProblematica);
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
