package filter;

import entity.Persona;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonaLoginValidator implements ConstraintValidator<ValidPersonaLogin, Persona> {

    private ValidPersonaLogin validPersonaLogin;

    @Override
    public void initialize(ValidPersonaLogin constraintAnnotation) {
        this.validPersonaLogin = constraintAnnotation;
    }

    @Override
    public boolean isValid(Persona user, ConstraintValidatorContext context) {
        String mensaje = "";
        boolean esValido = false;

        if(user.email.isEmpty()){
            mensaje = "El email no puede estar vacio";
            esValido = false;
        }else if(user.pass.isEmpty()){
            mensaje = "La contraseña no puede estar vacia";
            esValido = false;
        }

        context.buildConstraintViolationWithTemplate(mensaje).addConstraintViolation();
        return esValido;
    }
}