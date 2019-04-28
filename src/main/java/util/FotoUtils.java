package util;

import entity.Nodo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FotoUtils {

    public final static String DIRECTORIO = "C:/Proyectos/Proyecto de grado/inv/nodos";

    public static String guardarFoto(Nodo nuevoNodo, InputStream foto, String extensionFoto) throws IOException {
        nuevoNodo.urlFoto = FotoUtils.crearDirectorioYDarRuta(nuevoNodo.idProblematica, nuevoNodo.email, nuevoNodo.id, extensionFoto);
        Files.copy(foto, Paths.get(nuevoNodo.urlFoto));
        return nuevoNodo.urlFoto;
    }

    private static String crearDirectorioYDarRuta(int idProblematica, String emailUsuario, int idNodo, String extension){
        String ruta = DIRECTORIO + "/" + idProblematica + "/" + emailUsuario + "/" + idNodo + "." + extension;
        File rutaFile = new File(ruta);
        if(rutaFile.exists())rutaFile.delete();
        rutaFile.getParentFile().mkdirs();

        return ruta;
    }

    public static boolean eliminarFoto(String urlFoto) {
        File foto = new File(urlFoto);
        return foto.delete();
    }
}
