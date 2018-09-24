package com.darmaso.spring.kotlinwebflux.handler

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class DemoHandler {
    fun getDemo(req: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(Flux.just("{\"message\":\"hello kotlin\"}"),String::class.java)
    }
}