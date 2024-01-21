package jmantello.secretrecipeapi.controller

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class FaviconController {
    @GetMapping("favicon.ico")
    @ResponseBody
    fun favicon(): ByteArray {
        return ClassPathResource("static/favicon.ico").getInputStream().readAllBytes()
    }
}