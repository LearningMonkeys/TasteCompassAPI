package com.service.controller

import com.service.dto.OpenaiEmbeddingRequest
import com.service.dto.OpenaiEmbeddingResponse
import com.service.dto.upload.OpenaiUploadFileResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.io.File

@RestController
@RequestMapping("/test")
class OpenaiTestController {
    @Value("\${spring.ai.openai.api-key}")
    lateinit var apiKey: String
    @Value("\${spring.ai.openai.embedding.options.model}")
    lateinit var embeddingModel: String
    @Value("\${spring.ai.openai.base-url}")
    lateinit var openaiBaseUrl: String
    @Value("\${spring.ai.openai.embedding.embeddings-path}")
    lateinit var embeddingUrl: String
    @Value("\${spring.ai.openai.upload.url}")
    lateinit var uploadFileUrl: String

    private val restTemplate = RestTemplate()

    @PostMapping("/openai/embedding")
    fun requestEmbeddingApi(@RequestBody request: OpenaiEmbeddingRequest): ResponseEntity<OpenaiEmbeddingResponse> {
        val apiUrl = openaiBaseUrl + embeddingUrl
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer $apiKey")
        val body = mapOf(
                "input" to request.input,
                "model" to embeddingModel
        )

        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForEntity(apiUrl, entity, OpenaiEmbeddingResponse::class.java)
        return ResponseEntity.ok(response.body)
    }

    @PostMapping("/openai/uploadFile")
    fun uploadFileApi(): ResponseEntity<OpenaiUploadFileResponse> {
        val apiUrl = openaiBaseUrl + uploadFileUrl
        println("apiUrl $apiUrl")
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $apiKey")
        }

        val body = MultipartBodyBuilder().apply {
            part("file", FileSystemResource("Application/src/main/resources/batchDataFile.jsonl"), MediaType.MULTIPART_FORM_DATA)
            part("purpose", "batch")
        }.build()

        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForEntity(apiUrl, entity, OpenaiUploadFileResponse::class.java)
        return ResponseEntity.ok(response.body)
    }
}
