package usecase

import dao.DaoPersona
import util.CorreoUtils

class CorreoUseCase(val daoPersona: DaoPersona, val correoUtils: CorreoUtils){

    fun enviarCorreo(email: String): Boolean{
        val optionalPersona = daoPersona.darPersona(email)
        return if(optionalPersona.isPresent){
            correoUtils.enviarA(optionalPersona.get())
        }else{
            false
        }
    }
}