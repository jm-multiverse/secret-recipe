package jmantello.secretrecipeapi.annotations

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPasswordValidator::class])
annotation class ValidPassword(
    val message: String = "Invalid password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidPasswordValidator : ConstraintValidator<ValidPassword, String> {

    override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
        if (password == null) {
            return false // or true, based on your requirement for null values
        }

        val lengthCheck = password.length in 8..100
        val lowerCaseCheck = password.any { it.isLowerCase() }
        val upperCaseCheck = password.any { it.isUpperCase() }
        val digitCheck = password.any { it.isDigit() }
        val specialCharCheck = password.any { it in "!@#\$%^&*()-_=+\\|[\\]{};:'\",<.>/? " }
        val whitespaceCheck = password.none { it.isWhitespace() }

        return lengthCheck && lowerCaseCheck && upperCaseCheck && digitCheck && specialCharCheck && whitespaceCheck
    }
}