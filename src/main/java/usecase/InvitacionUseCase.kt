package usecase

import dao.DaoInvitacion
import entity.Error
import entity.Invitacion
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class InvitacionUseCase(val daoInvitacion : DaoInvitacion){

    fun aceptarInvitacion(invitacion: Invitacion, idInvitacion: String): Boolean {
        return try {
            daoInvitacion.aceptarInvitacion(invitacion, idInvitacion)
        }catch (e : UnableToExecuteStatementException){
            e.printStackTrace() //TODO: Cambiar esto por Loggers
            return false
        }
    }

    fun rechazarInvitacion(invitacion: Invitacion, idInvitacion: String): Boolean {
        return try {
            daoInvitacion.rechazarInvitacion(invitacion, idInvitacion)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace() //TODO: Cambiar esto por Loggers
            return false
        }
    }

    fun eliminarInvitacion(invitacion: Invitacion, idInvitacion: String): Boolean {
        return try {
            daoInvitacion.aceptarInvitacion(invitacion, idInvitacion)
        }catch (e: UnableToExecuteStatementException){
            e.printStackTrace() //TODO: Cambiar esto por Loggers
            return false
        }
    }

    fun darPersonasInvitadas(emailRemitente: String, idProblematica: Int) = daoInvitacion.darPersonasInvitadas(emailRemitente, idProblematica)

    fun hacerInvitacion(invitacion: Invitacion): Any {
        val invitacion = daoInvitacion.agregarInvitacion(invitacion)
        return invitacion ?: Error(arrayOf("Verifique los parametros ingresados"))
    }

    fun darInvitacionesVigentes(emailDestinatario: String) = daoInvitacion.darInvitacionesVigentesRecibidas(emailDestinatario)
}
