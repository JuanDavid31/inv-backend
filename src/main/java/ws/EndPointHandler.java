package ws;

import entity.SesionCliente;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EndPointHandler {

    private static EndPointHandler instance;
    public static Map<Long, Map<String, SesionCliente>> salasActivas = new ConcurrentHashMap<>();//TODO: Esta variable debe ser ultra thread safe

    public static void agregarCliente(Session sesion) {
        Long idProblematica = darIdProblematica(sesion);
        salasActivas.computeIfAbsent(idProblematica, k -> new HashMap<String, SesionCliente>());
        salasActivas.get(idProblematica)
            .put(String.valueOf(sesion.hashCode()), new SesionCliente(sesion, "", ""));
    }

    private static Long darIdProblematica(Session sesion) {
        String idProblematica = sesion.getUpgradeRequest().getParameterMap().get("idProblematica").get(0);
        System.out.println("idProblematica de la url - " + idProblematica);//Imprime [1] si se usa esta ruta ws://localhost:8080/colaboracion?idProblematica=1
        return Long.parseLong(idProblematica);
    }

    public static void eliminarCliente(Session sesion){
        Long idProblematica = darIdProblematica(sesion);
        Map grupo = salasActivas.get(idProblematica);
        grupo.remove(String.valueOf(sesion.hashCode()));
        if(grupo.size() == 0){
            salasActivas.remove(idProblematica);
        }
    }

    /**
     * Retorna el map de sesiones cliente de la sala con la problematica en la url de la sesión.
     * @param sesion
     * @return
     */
    public static Map<String, SesionCliente> darSesionesPorGrupo(Session sesion) {
        Long idProblematica = darIdProblematica(sesion);
        return salasActivas.get(idProblematica);
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
