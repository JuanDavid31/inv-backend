package usecase

import dao.DaoProblematica
import entity.Error
import entity.Problematica

class ProblematicaUseCase(private val daoProblematica: DaoProblematica) {

    fun agregarProblematicaPorPersona(email: String, problematica: Problematica): Any {
        val nuevaProblematica = daoProblematica.agregarProblematicaPorPersona(email, problematica)
        return nuevaProblematica ?: Error(arrayOf("No se pudo crear la problematica, verifique los parametros ingresados"))
    }

    fun darProblematicasPorPersona(email: String): List<Problematica> {
        return daoProblematica.darProblematicasPorPersona(email)
    }

    fun avanzarFase(idProblematica: Int): Any {
        val faseActual = daoProblematica.darFase(idProblematica)
        if (!faseActual.isPresent) return Error(arrayOf("La problematica dada no existe"))
        daoProblematica.avanzarFaseProblematica(idProblematica)
        if(faseActual.get() == 2){
            //TODO: Enviar un mensaje a todos los websockets para que ya no puedan editar.
        }
        return object { val mensaje = "Se avanzó exitosamente la fase de la problematica" }
    }
}