package usecase;

import dao.DaoNodo;
import entity.Nodo;
import util.FotoUtils;

import java.io.IOException;
import java.io.InputStream;

public class FotoUseCase {

    private final DaoNodo daoNodo;

    public FotoUseCase(DaoNodo daoNodo){
        this.daoNodo = daoNodo;
    }

    public Nodo guardarFoto(Nodo nodo, InputStream foto, String extensionFoto){
        if(!FotoUtils.extensionValida(extensionFoto))return null;
        try {
            nodo.id = daoNodo.agregarNodo(nodo);
            nodo.urlFoto = FotoUtils.guardarFotoEnDirectorioYDarUrl(nodo, foto, extensionFoto);
            nodo.rutaFoto = FotoUtils.darRuta(nodo, extensionFoto);
            daoNodo.actualizarNodo(nodo);
            return nodo;
        } catch (IOException e) {
            e.printStackTrace();
            if(nodo.id == 0)daoNodo.eliminarNodo(nodo.id);
            return null;
        }
    }

    public boolean eliminarNodoYFoto(int idNodo) {
        Nodo nodo = daoNodo.eliminarNodo(idNodo);
        return FotoUtils.eliminarFoto(nodo.urlFoto);
    }
}