package usecase;

import dao.DaoProblematica;
import entity.Problematica;

import java.util.List;
import java.util.Optional;

public class ProblematicaUseCase {

    private final DaoProblematica daoProblematica;

    public ProblematicaUseCase(DaoProblematica daoProblematica){
        this.daoProblematica = daoProblematica;
    }

    public Problematica agregarProblematicaPorPersona(String email, Problematica problematica) {
        return daoProblematica.agregarProblematicaPorPersona(email, problematica);
    }

    public List<Problematica> darProblematicasPorPersona(String email){
        return daoProblematica.darProblematicasPorPersona(email);
    }

    public Optional<Integer> darFase(int idProblematica){
        return daoProblematica.darFase(idProblematica);
    }

    public boolean avanzarFase(int idProblematica){
        Optional<Integer> faseActual = daoProblematica.darFase(idProblematica);
        if(faseActual.isPresent() && faseActual.get() == 2){
            boolean seAvanzo = daoProblematica.avanzarFaseProblematica(idProblematica);
            if (!seAvanzo)return false;
            //TODO: Enviar un mensaje a todos los websockets para que ya no puedan editar.
        }
        return true;
    }
}