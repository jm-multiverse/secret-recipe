package jmantello.secretrecipeapi.annotations

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidEmailValidator::class])
annotation class ValidEmail(
    val message: String = "Invalid email format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidEmailValidator : ConstraintValidator<ValidEmail, String> {
    companion object {
        // Regular expression for email validation
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }

    override fun isValid(email: String?, context: ConstraintValidatorContext): Boolean {
        if (email == null) {
            return false // or true, based on your requirement for null values
        }

        return EMAIL_REGEX.matches(email)
    }
}