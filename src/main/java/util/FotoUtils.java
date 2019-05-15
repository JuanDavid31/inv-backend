package util;

import entity.Nodo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FotoUtils {
    //TODO: Las imagenes se estan guardando con .. antes de la extensión
    private final static String DIRECTORIO = "/home/nodos";
    private final static String DOMINIO = "localhost:8081/nodos";

    public static String guardarFotoEnDirectorioYDarUrl(Nodo nuevoNodo, InputStream foto, String extensionFoto) throws IOException {
        String rutaArchivo = FotoUtils.crearDirectorioYDarRutaArchivo(nuevoNodo, extensionFoto);
        Files.copy(foto, Paths.get(DIRECTORIO + rutaArchivo));
        return DOMINIO + rutaArchivo;
    }

    private static String crearDirectorioYDarRutaArchivo(Nodo nodo, String extension){
        String rutaArchivo = darRuta(nodo, extension);
        String ruta = DIRECTORIO + rutaArchivo;
        File rutaFile = new File(ruta);
        if(rutaFile.exists())rutaFile.delete();
        rutaFile.getParentFile().mkdirs();

        return rutaArchivo;
    }

    public static String darRuta(Nodo nodo, String extension){
        return "/" + nodo.idProblematica + "/" + nodo.email + "/" + nodo.id + "." + extension;
    }

    public static boolean eliminarFoto(String rutaFoto) {
        return new File(DIRECTORIO + rutaFoto).delete();
    }

    public static boolean extensionValida(String extensionFoto) {
        if(extensionFoto.contains(".")) return false;
        switch (extensionFoto.toUpperCase()){
            case "PNG":
                return true;
            case "JPG":
                return true;
            case "JPEG":
                return true;
            default:
                return false;
        }
    }
}
