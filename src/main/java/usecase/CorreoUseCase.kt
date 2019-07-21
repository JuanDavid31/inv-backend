package usecase

import dao.DaoPersona
import entity.Error
import util.CorreoUtils

class CorreoUseCase(val daoPersona: DaoPersona, val correoUtils: CorreoUtils){

    fun enviarCorreo(email: String): Any{
        val optionalPersona = daoPersona.darPersona(email)
        return if(optionalPersona.isPresent){
            correoUtils.enviarPassA(optionalPersona.get())
        }else{
            Error(arrayOf("Este correo no existe"))
        }
    }
}