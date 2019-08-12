package usecase

import dao.DaoPersona
import entity.Error
import entity.Mensaje
import util.CorreoUtils

class CorreoUseCase(val daoPersona: DaoPersona, val correoUtils: CorreoUtils){

    fun enviarCorreo(email: String): Any{
        val optionalPersona = daoPersona.darPersona(email)
        return if(optionalPersona.isPresent){
            correoUtils.enviarPassA(optionalPersona.get())
            Mensaje("Correo enviado con exito.")
        }else{
            Error(arrayOf("Este correo no esta registrado"))
        }
    }
}