package com.darmaso.spring.kotlinwebflux

import com.darmaso.spring.kotlinwebflux.handler.DemoHandler
import com.darmaso.spring.kotlinwebflux.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class Router(private val demoHandler: DemoHandler, private val userHandler: UserHandler) {
    @Bean
    fun apiRouter() = router {
        accept(MediaType.APPLICATION_JSON_UTF8).nest {
            GET("/v1/demo", demoHandler::getDemo)
            GET("/v1/users", userHandler::findAll)
        }
        accept(MediaType.TEXT_EVENT_STREAM).nest {
            GET("/stream/users", userHandler::streamOneSec)
        }
    }
}