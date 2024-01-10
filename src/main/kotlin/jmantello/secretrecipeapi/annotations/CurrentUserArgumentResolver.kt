package jmantello.secretrecipeapi.annotations

import jmantello.secretrecipeapi.service.AuthenticationService
import jmantello.secretrecipeapi.util.Result.Error
import jmantello.secretrecipeapi.util.Result.Success
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CurrentUserArgumentResolver(
    private val authenticationService: AuthenticationService
) :
    HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentUserDTO::class.java)
                || parameter.hasParameterAnnotation(CurrentUserId::class.java)
                || parameter.hasParameterAnnotation(CurrentUserEntity::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        // Call appropriate authentication method based on annotation
        val result = when {
            parameter.hasParameterAnnotation(CurrentUserDTO::class.java) -> authenticationService.getCurrentUserDTO()
            parameter.hasParameterAnnotation(CurrentUserId::class.java) -> authenticationService.getCurrentUserId()
            parameter.hasParameterAnnotation(CurrentUserEntity::class.java) -> authenticationService.getCurrentUserEntity() // If authenticationService.getCurrentUserEntity() requires transaction, try using TransactionTemplate to execute the method
            else -> throw IllegalStateException("Unsupported annotation")
        }

        return when (result) {
            is Success -> result.data
            is Error -> throw IllegalStateException(result.message)
        }
    }
}