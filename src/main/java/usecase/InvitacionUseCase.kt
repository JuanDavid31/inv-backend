package usecase

import dao.DaoInvitacion
import dao.DaoPersona
import entity.Error
import entity.Invitacion
import rest.sse.EventPublisher
import util.SingletonUtils
import java.util.concurrent.CompletableFuture

class InvitacionUseCase(private val daoInvitacion: DaoInvitacion,
                        private val daoPersona: DaoPersona,
                        private val eventPublisher: EventPublisher){

    fun hacerInvitacion(invitacion: Invitacion): Any {
        val nuevaInvitacion = daoInvitacion.agregarInvitacion(invitacion)
        return if(nuevaInvitacion != null){
            enviarNotificacion(nuevaInvitacion)
            nuevaInvitacion
        }else{
            Error(arrayOf("Verifique los parametros ingresados"))
        }
    }

    private fun enviarNotificacion(invitacion: Invitacion) {
        CompletableFuture.runAsync {
            val invitacionAEnviar = daoInvitacion.darInvitacionesVigentesRecibidas(invitacion.emailDestinatario)
                    .find { invitacion.idProblematica == it["idProblematica"] }
            val jsonHash: Map<String, Any?> = mapOf("accion" to "Invitacion recibida", "invitacion" to invitacionAEnviar)
            eventPublisher.enviarInvitacion(jsonHash, invitacion.emailDestinatario)
        }.thenRun { println("Evento de invitación enviado.") }
    }

    fun aceptarInvitacion(invitacion: Invitacion, idInvitacion: String): Any {
        val nuevaInvitacion = daoInvitacion.aceptarInvitacion(invitacion, idInvitacion)
        return if(nuevaInvitacion != null){
            enviarRespuestaAInterventores(nuevaInvitacion)
            nuevaInvitacion
        }else{
            Error(arrayOf("Verifique los parametros ingresados"))
        }
    }

    fun rechazarInvitacion(invitacion: Invitacion, idInvitacion: String): Any {
        val invitacion = daoInvitacion.rechazarInvitacion(invitacion)
        return if(invitacion != null){
            enviarRespuestaAInterventores(invitacion)
            invitacion
        }else{
            Error(arrayOf("Verifique los parametros ingresados"))
        }
    }

    fun eliminarInvitacion(idInvitacion: String): Any {
        val seElimino = daoInvitacion.eliminarInvitacion(idInvitacion)
        return if (seElimino) Unit else Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun darPersonasInvitadas(emailRemitente: String, idProblematica: Int) = daoInvitacion.darPersonasInvitadas(emailRemitente, idProblematica)

    fun darInvitacionesVigentes(emailDestinatario: String) = daoInvitacion.darInvitacionesVigentesRecibidas(emailDestinatario)

    fun enviarRespuestaAInterventores(invitacion: Invitacion){
        val jsonHash = hashMapOf("accion" to "Invitacion respondida", "invitacion" to invitacion)
        val sesionId = SingletonUtils.darIdSesion()
        CompletableFuture.runAsync {
            val emailInterventores: List<String> = daoPersona.darInterventores(invitacion.idProblematica)
            eventPublisher.difundirEventoDeRespuestaAInterventores(sesionId, jsonHash, emailInterventores)
        }.thenRun{ println("Evento de invitacion respondida enviado") }
    }
}
