package usecase

import dao.DaoPersona
import util.CorreoUtils

class CorreoUseCase(val daoPersona: DaoPersona, val correoUtils: CorreoUtils){

    fun enviarCorreo(email: String): Boolean{
        val optPersona = daoPersona.darPersona(email)
        return if(optPersona.isPresent){
            correoUtils.enviarA(optPersona.get())
        }else{
            false
        }
    }
}