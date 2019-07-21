package dao

import entity.Persona
import org.jdbi.v3.core.Jdbi
import java.util.*

class DaoPersona(val jdbi: Jdbi){

    fun agregarPersona(persona: Persona): Persona{
        return jdbi.withHandle<Persona, Exception>{
            it.createUpdate("INSERT INTO PERSONA(a_email, d_nombre, a_pass_hasheado) VALUES(:email, :nombre, :pass)")
                    .bindBean(persona)
                    .executeAndReturnGeneratedKeys()
                    .mapToBean(Persona::class.java)
                    .findOnly()
        }
    }

    fun darPersonaPorCredenciales(persona: Persona): Optional<Persona> {
        return jdbi.withHandle<Optional<Persona>, Exception>{
            it.createQuery("Select d_nombre, a_email FROM PERSONA WHERE a_email = :email AND a_pass_hasheado = :pass")
                    .bindBean(persona)
                    .mapToBean(Persona::class.java)
                    .findFirst()
        }
    }

    fun darPersona(email: String): Optional<Persona>{
        return jdbi.withHandle<Optional<Persona>, Exception> {
            it.createQuery("SELECT * FROM PERSONA WHERE a_email = :email")
                    .bind("email", email)
                    .mapToBean(Persona::class.java)
                    .findFirst()
        }
    }

    fun verificarExistencia(email: String): Boolean {
        return jdbi.withHandle<Boolean, Exception> {
            it.createQuery("SELECT * FROM PERSONA WHERE upper(a_email) = upper(:email)")
                    .bind("email", email)
                    .mapToBean(Persona::class.java)
                    .findFirst().isPresent()
        }
    }
}