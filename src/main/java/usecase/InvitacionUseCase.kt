package usecase

import dao.DaoInvitacion
import entity.Error
import entity.Invitacion
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class InvitacionUseCase(val daoInvitacion : DaoInvitacion){

    fun hacerInvitacion(invitacion: Invitacion): Any {
        val invitacion = daoInvitacion.agregarInvitacion(invitacion)
        return invitacion ?: Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun aceptarInvitacion(invitacion: Invitacion, idInvitacion: String): Any {
        val seAcepto = daoInvitacion.aceptarInvitacion(invitacion, idInvitacion)
        return if (seAcepto) Unit else Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun rechazarInvitacion(invitacion: Invitacion, idInvitacion: String): Any {
        val seRechazo = daoInvitacion.rechazarInvitacion(invitacion, idInvitacion)
        return if (seRechazo) Unit else Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun eliminarInvitacion(invitacion: Invitacion, idInvitacion: String): Any {
        val seElimino = daoInvitacion.eliminarInvitacion(invitacion, idInvitacion)
        return if (seElimino) Unit else Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun darPersonasInvitadas(emailRemitente: String, idProblematica: Int) = daoInvitacion.darPersonasInvitadas(emailRemitente, idProblematica)

    fun darInvitacionesVigentes(emailDestinatario: String) = daoInvitacion.darInvitacionesVigentesRecibidas(emailDestinatario)
}
