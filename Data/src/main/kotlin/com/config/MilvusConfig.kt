package com.config

import com.common.Constants.ENDPOINT_URI
import com.common.Constants.BEAR_TOKEN
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.client.ConnectConfig

class MilvusConfig {
    fun milvusClient(): MilvusClientV2 {
        return MilvusClientV2(
            ConnectConfig.builder()
                .uri(ENDPOINT_URI)
                .token(BEAR_TOKEN)
                .build()
        )
    }
}