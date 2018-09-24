package com.darmaso.spring.kotlinwebflux

import com.darmaso.spring.kotlinwebflux.domain.User
import com.darmaso.spring.kotlinwebflux.handler.DemoHandler
import com.darmaso.spring.kotlinwebflux.handler.UserHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.mockito.Mockito.`when`
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux

/**
 * 参考
 * http://blog.soushi.me/entry/2017/06/22/123942
 */
@RunWith(SpringRunner::class)
@SpringBootTest
class KotlinwebfluxApplicationTests {
    lateinit var client: WebTestClient
    lateinit var demoHandler: DemoHandler
    lateinit var userHandler: UserHandler

    private val log = LoggerFactory.getLogger(KotlinwebfluxApplicationTests::class.java)

    private val users = Flux.just(
            User("1", "tarou", "1999-09-09"),
            User("2", "hanako", "2002-02-02"),
            User("3", "tonkichi", "1988-08-08")
    )

    @Before
    fun before() {
        userHandler = Mockito.mock(UserHandler::class.java).apply {
            `when`(findAll(KotlinModule.any())).thenReturn(
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .body(users, User::class.java)
            )
        }
        demoHandler = Mockito.mock(DemoHandler::class.java).apply {
            `when`(getDemo(KotlinModule.any())).thenReturn(
                    ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                            .body(Flux.just("{\"message\":\"hello kotlin\"}"),String::class.java))
        }
        val router = Router(demoHandler, userHandler)
        client = WebTestClient.bindToRouterFunction(router.apiRouter()).build()
    }

    @Test
    fun testGetUsers() {
        client.get().uri("/v1/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("tarou")
                .jsonPath("$[1].name").isEqualTo("hanako")
                .jsonPath("$[2].name").isEqualTo("tonkichi")
    }

    @Test
    fun testDemo() {
        client.get().uri("/v1/demo")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody().json("{\"message\":\"hello kotlin\"}")
    }

}
