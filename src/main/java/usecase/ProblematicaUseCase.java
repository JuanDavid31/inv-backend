package usecase;

import dao.DaoProblematica;

public class ProblematicaUseCase {

    private final DaoProblematica daoProblematica;

    public ProblematicaUseCase(DaoProblematica daoProblematica){
        this.daoProblematica = daoProblematica;
    }

    public boolean avanzarFase(int idProblematica){
        int faseActual = daoProblematica.darFase(idProblematica);
        if(faseActual == 2){
            //TODO: Enviar un mensaje a todos los websockets para que ya no puedan editar.
        }
        return daoProblematica.avanzarFaseProblematica(idProblematica);
    }
}
