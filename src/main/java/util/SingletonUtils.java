package util;

import usecase.GrupoUseCase;
import usecase.NodoUseCase;

import java.util.HashMap;
import java.util.Map;

/**
 * La existencia de esta clase es para evitar añadir Dagger 2 por el momento.
 * Solo es usada en la clase EndPoint
 */
public class SingletonUtils {

    static Map<String, Object> singletons = new HashMap<>();

    private static ThreadLocal<String> idSesion = new ThreadLocal<>();

    public static GrupoUseCase guardarGrupoUseCase(GrupoUseCase grupoUseCase){
        singletons.put("grupoUseCase", grupoUseCase);
        return grupoUseCase;
    }

    public static GrupoUseCase darGrupoUseCase(){
        return (GrupoUseCase) singletons.get("grupoUseCase");
    }

    public static NodoUseCase guardarNodoUseCase(NodoUseCase nodoUseCase){
        singletons.put("nodoUseCase", nodoUseCase);
        return nodoUseCase;
    }

    public static NodoUseCase darNodoUseCase(){
        return (NodoUseCase) singletons.get("nodoUseCase");
    }

    public static String darIdSesion(){ return idSesion.get(); }

    public static void guardarIdSesion(String idSession){
        idSesion.set(idSession);
    }
}
