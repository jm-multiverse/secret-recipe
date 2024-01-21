package jmantello.secretrecipeapi.config.monitoring

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerMapping
import java.util.concurrent.TimeUnit

@Component
class RequestResponseMonitoringFilter(private val meterRegistry: MeterRegistry) : OncePerRequestFilter() {
    // Define custom metrics for monitoring the application
    val requestCounter = Counter.builder("http.requests.count")
    val requestTimer = Timer.builder("http.requests.time")
    val successCounter = Counter.builder("http.responses.success")
    val errorCounter = Counter.builder("http.responses.error")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Normalized Endpoint Path
        val normalizedPath: String = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) as? String
            ?: request.requestURI

        // Count Request
        requestCounter
            .tag("method", request.method)
            .tag("endpoint", normalizedPath)
            .register(meterRegistry)
            .increment()

        // Start Timer
        val startTime = System.currentTimeMillis()
        try {
            // Execute Request
            filterChain.doFilter(request, response)

            // Check Error
            if (response.status >= 400) {
                // Log Error
                logger.error("Error Response: ${response.status} for ${request.method} ${request.requestURI}")
                // Count Error
                errorCounter
                    .tag("method", request.method)
                    .tag("endpoint", normalizedPath)
                    .tag("status", response.status.toString())
                    .register(meterRegistry)
                    .increment()
            } else {
                // Count Success
                successCounter
                    .tag("method", request.method)
                    .tag("endpoint", normalizedPath)
                    .register(meterRegistry)
                    .increment()
            }
        } catch (e: Exception) {
            // Count Error
            errorCounter
                .tag("method", request.method)
                .tag("endpoint", normalizedPath)
                .tag("status", response.status.toString())
                .register(meterRegistry)
                .increment()

            // Log exception info
            logger.error("Exception during processing ${request.method} ${request.requestURI}", e)
        } finally {
            // Record duration
            val duration = System.currentTimeMillis() - startTime
            requestTimer
                .tag("method", request.method)
                .tag("endpoint", normalizedPath)
                .register(meterRegistry).record(duration, TimeUnit.MILLISECONDS)
        }
    }
}