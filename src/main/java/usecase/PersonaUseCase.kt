package usecase

import dao.DaoPersona
import entity.Persona
import entity.Error
import util.CorreoUtils
import util.JWTUtils

class PersonaUseCase(val correoUtils: CorreoUtils, val jwtUtils: JWTUtils, val daoPersona: DaoPersona){

    fun agregarPersona(persona: Persona): Any {
        val correoRegistrado = daoPersona.verificarExistencia(persona.email)
        val correoValido = verificar(persona.email)
        println("Correo valido - $correoValido")
        return if(!correoValido){
            Error(arrayOf("El correo no es valido"))
        }else if(correoRegistrado){
            Error(arrayOf("Este Correo ya esta en uso"))
        }else{
            daoPersona.agregarPersona(persona)
            object {
                val data = Data(persona.nombres, persona.apellidos, persona.email)
                val token = jwtUtils.darToken(persona)
            }
        }
    }

    private fun verificar(correo: String): Boolean{
        return correoUtils.existe(correo)
    }

    fun darPersonaPorCredenciales(persona: Persona): Any{
        val optionalPersona = daoPersona.darPersonaPorCredenciales(persona)
        return if(optionalPersona.isPresent)
            object {
                val data = Data(optionalPersona.get().nombres, optionalPersona.get().apellidos, optionalPersona.get().email)
                val token = jwtUtils.darToken(persona)
            }
        else
            Error(arrayOf("Email o contraseña invalido"))
    }

    fun darPersonasPorCorreo(email: String): List<Persona> {
        return daoPersona.darPersonas(email)
    }
}

data class Data(val nombres: String, val apellidos: String,  val email: String)