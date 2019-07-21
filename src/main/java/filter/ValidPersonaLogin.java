package filter;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PersonaLoginValidator.class)
@Documented
public @interface ValidPersonaLogin {
    String message () default "Hubo un error";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}

