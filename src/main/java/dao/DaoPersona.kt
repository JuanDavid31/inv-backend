package dao

import entity.Persona
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl
import org.jdbi.v3.core.Jdbi
import java.util.*

class DaoPersona(val jdbi: Jdbi){

    fun agregarPersona(persona: Persona): Persona{
        return jdbi.withHandle<Persona, Exception>{
            it.createUpdate("INSERT INTO PERSONA(a_email, d_nombres, d_apellidos, a_pass_hasheado) VALUES(:email, :nombres, :apellidos, :pass)")
                    .bindBean(persona)
                    .executeAndReturnGeneratedKeys()
                    .mapToBean(Persona::class.java)
                    .findOnly()
        }
    }

    fun darPersonaPorCredenciales(persona: Persona): Optional<Persona> {
        return jdbi.withHandle<Optional<Persona>, Exception>{
            it.createQuery("SELECT d_nombres, d_apellidos, a_email FROM PERSONA WHERE a_email = :email AND a_pass_hasheado = :pass")
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

    fun darPersonas(email: String): List<Persona> {
        return jdbi.withHandle<List<Persona>, java.lang.Exception>{
            it.createQuery("SELECT a_email, d_nombres, d_apellidos FROM persona WHERE a_email like :correo")
                    .bind("correo", "%$email%")
                    .setMaxRows(5)
                    .mapToBean(Persona::class.java)
                    .list()
        }
    }
}