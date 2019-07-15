package usecase;

import dao.DaoNodo;
import entity.Nodo;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
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
        try{
            Nodo nodo = daoNodo.eliminarNodo(idNodo);
            return FotoUtils.eliminarFoto(nodo.rutaFoto);
        }catch (UnableToExecuteStatementException e){
            e.printStackTrace();
            return false;
        }

    }
}