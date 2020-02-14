package usecase

import dao.DaoProblematica
import entity.Error
import entity.Mensaje
import entity.Problematica
import rest.sse.EventPublisher
import util.SingletonUtils
import java.util.*
import java.util.concurrent.CompletableFuture

class ProblematicaUseCase(private val daoProblematica: DaoProblematica, private val eventPublisher: EventPublisher) {

    fun agregarProblematicaPorPersona(email: String, problematica: Problematica): Any {
        val nuevaProblematica = daoProblematica.agregarProblematicaPorPersona(email, problematica)
        return nuevaProblematica ?: Error(arrayOf("No se pudo crear la problematica, verifique los parametros ingresados"))
    }

    fun darProblematicasPorPersona(email: String): List<Problematica> {
        return daoProblematica.darProblematicasPorPersona(email)
    }

    fun darEstadoProblematica(idProblematica: Int): Any{
        val faseOptional = daoProblematica.darFase(idProblematica)
        if (!faseOptional.isPresent) return Error(arrayOf("La problematica dada no existe"))
        return when(faseOptional.get()){
            0 -> {
                object {
                    val fase: Int = faseOptional.get()
                    val cantidadParticipantes: Int = daoProblematica.darCantidadParticipantes(idProblematica)
                }
            }
            1 -> {
                object{
                    val fase: Int = faseOptional.get()
                    val cantidadNodos: Int = daoProblematica.darCantidadNodos(idProblematica)
                }
            }
            2 -> {
                object {
                    val fase: Int = faseOptional.get()
                    val cantidadNodos: Int = daoProblematica.darCantidadNodos(idProblematica)
                    val cantidadGrupos: Int = daoProblematica.darCantidadGrupos(idProblematica)
                }
            }
            3 -> {
                object {
                    val fase: Int = faseOptional.get()
                    val cantidadParticipantes: Int = daoProblematica.darCantidadParticipantes(idProblematica)
                    val cantidadGrupos: Int = daoProblematica.darCantidadGrupos(idProblematica)
                    val cantidadReacciones: Int = daoProblematica.darCantidadReacciones(idProblematica)
                }
            }
            4 -> {
                object {
                    val fase: Int = faseOptional.get()
                    val cantidadParticipantes: Int = daoProblematica.darCantidadParticipantes(idProblematica)
                    val cantidadGrupos: Int = daoProblematica.darCantidadGrupos(idProblematica)
                    val cantidadEscritos: Int = daoProblematica.darCantidadEscritos(idProblematica)
                }
            }
            5 -> {
                Mensaje("El desarrollo de la problematica ha finalizado")
            }
            else -> Error(arrayOf("Ha ocurrido un error, por favor intentelo de nuevo."))
        }
    }

    fun avanzarFase(idProblematica: Int): Any {
        val faseActual = daoProblematica.darFase(idProblematica)
        if(!faseActual.isPresent) return Error(arrayOf("La problematica dada no existe"))
        if(!daoProblematica.avanzarFaseProblematica(idProblematica)) return Error(arrayOf("La problematica ya esta en su fase final."))

        difundirAParticipantes(idProblematica, faseActual);

        if(faseActual.get() == 2){
            //TODO: Enviar un mensaje a todos los websockets para que ya no puedan editar.
        }
        return faseActual.get()
    }

    private fun difundirAParticipantes(idProblematica: Int, faseActual: Optional<Int>) {
        val json = hashMapOf("accion" to "Cambio fase problematica",
                "idProblematica" to idProblematica,
                "nuevaFase" to faseActual.get() + 1)

        val idSesion = SingletonUtils.darIdSesion()

        CompletableFuture.runAsync {
            val participantes = daoProblematica.darParticipantesPorProblematica(idProblematica)
            eventPublisher.difundirAvanceFaseAParticipantesMenosA(idSesion, json, participantes);
        }.thenRun { println("Evento de fase avanzada enviado.") }
    }

    fun darProblematicasTerminadasPorPersona(email: String): List<Problematica>
        = daoProblematica.darProblematicasTerminadasPorPersona(email)

}