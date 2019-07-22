package filter

import entity.Persona

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class PersonaLoginValidator : ConstraintValidator<ValidPersonaLogin, Persona> {

    private var validPersonaLogin: ValidPersonaLogin? = null

    override fun initialize(constraintAnnotation: ValidPersonaLogin) {
        this.validPersonaLogin = constraintAnnotation
    }

    override fun isValid(user: Persona, context: ConstraintValidatorContext): Boolean {
        var mensaje = ""
        var esValido = true

        if (user.email.isEmpty()) {
            mensaje = "El email no puede estar vacio"
            esValido = false
        } else if (user.pass.isEmpty()) {
            mensaje = "La contraseña no puede estar vacia"
            esValido = false
        }

        context.buildConstraintViolationWithTemplate(mensaje).addConstraintViolation()
        return esValido
    }
}