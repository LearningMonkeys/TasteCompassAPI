package com.service.dto.upload

data class OpenaiUploadFileResponse(
        val id: String,
        val bytes: Int,
        val created_at: Int,
        val filename: String,
        val `object`: String,
        val purpose: String
)