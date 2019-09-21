package usecase

import dao.DaoInvitacion
import entity.Error
import entity.Invitacion

class InvitacionUseCase(val daoInvitacion : DaoInvitacion){

    fun hacerInvitacion(invitacion: Invitacion): Any {
        val invitacion = daoInvitacion.agregarInvitacion(invitacion)
        return invitacion ?: Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun aceptarInvitacion(invitacion: Invitacion, idInvitacion: String): Any {
        val invitacion = daoInvitacion.aceptarInvitacion(invitacion, idInvitacion)
        return invitacion ?: Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun rechazarInvitacion(invitacion: Invitacion, idInvitacion: String): Any {
        val invitacion = daoInvitacion.rechazarInvitacion(invitacion, idInvitacion)
        return invitacion ?: Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun eliminarInvitacion(idInvitacion: String): Any {
        val seElimino = daoInvitacion.eliminarInvitacion(idInvitacion)
        return if (seElimino) Unit else Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun darPersonasInvitadas(emailRemitente: String, idProblematica: Int) = daoInvitacion.darPersonasInvitadas(emailRemitente, idProblematica)

    fun darInvitacionesVigentes(emailDestinatario: String) = daoInvitacion.darInvitacionesVigentesRecibidas(emailDestinatario)
}
