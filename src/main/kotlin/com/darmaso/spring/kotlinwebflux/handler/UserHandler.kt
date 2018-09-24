package com.darmaso.spring.kotlinwebflux.handler

import com.darmaso.spring.kotlinwebflux.domain.User
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToServerSentEvents
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class UserHandler {
    private val users = Flux.just(
            User("1", "tarou", "1999-09-09"),
            User("2", "hanako", "2002-02-02"),
            User("3", "tonkichi", "1988-08-08")
    )
    private val streamingUsers = Flux
            .zip(Flux.interval(Duration.ofSeconds(1)), users.repeat())
            .map { it.t2 }

    fun findAll(req: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(users, User::class.java)
    }

    fun streamOneSec(req: ServerRequest) = ServerResponse.ok()
            .bodyToServerSentEvents(streamingUsers)
}
