package filter

import javax.validation.Constraint
import javax.validation.Payload
import java.lang.annotation.*
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PersonaLoginValidator::class])
@Documented
annotation class ValidPersonaLogin(val message: String = "Hubo un error", val groups: Array<KClass<*>> = [], val payload: Array<KClass<out Payload>> = [])

