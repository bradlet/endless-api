package com.endless

import com.endless.models.EndlessApiResponse
import com.fasterxml.jackson.core.util.JacksonFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking

class ApplicationTest: StringSpec({

    "root responds with text" {
        testApplication {
            client.get("/").apply {
                status shouldBe HttpStatusCode.OK
                bodyAsText().isBlank() shouldBe false
            }
        }
    }

    "endless api responds as expected" {
        testApplication {
            client.get("/api/test").apply {
                status shouldBe HttpStatusCode.OK
                jacksonObjectMapper()
                    .readValue(bodyAsText(), EndlessApiResponse::class.java)
                    .apply {
                        path shouldBe "/api/test"
                        content shouldBe "hello world!"
                    }
            }
        }
    }

})
