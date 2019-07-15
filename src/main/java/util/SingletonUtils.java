package util;

import usecase.GrupoUseCase;

import java.util.HashMap;
import java.util.Map;

/**
 * La existencia de esta clase es para evitar añadir Dagger 2 por el momento.
 * Solo es usada en la clase EndPoint
 */
public class SingletonUtils {

    static Map<String, GrupoUseCase> singletons = new HashMap<>();

    public static GrupoUseCase guardarGrupoUseCase(GrupoUseCase grupoUseCase){
        singletons.put("grupoUseCase", grupoUseCase);
        return grupoUseCase;
    }

    public static GrupoUseCase darGrupoUseCase(){
        return singletons.get("grupoUseCase");
    }
}
