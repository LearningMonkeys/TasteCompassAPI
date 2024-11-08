package com.service

import com.entity.Restaurant
import com.service.repository.RestaurantRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.logging.Logger

class SampleProcessor : Processor {
    private var scope: CoroutineScope? = null
    private val repository = RestaurantRepository()

    override fun start(parentScope: CoroutineScope) {
        logger.info("start")

        scope = scope?.let {
            if(!it.isActive) {
                CoroutineScope(Dispatchers.IO)
            }
            it
        } ?: CoroutineScope(Dispatchers.IO)

        parentScope.launch {
            scope?.launch {
                logger.info("Start pipeline")
                val getData = fetchData(repository)
                val analyzeData = analyze(getData)
                store(analyzeData)

                // Check DB data for debugging
                repository.getAll().also {
                    logger.info("Total DB count : ${it.size}")
                    it.forEach { restaurant ->
                        logger.info(restaurant.toReadableString())
                    }
                }

                // Insert test
                scope?.launch {
                    (0  until 5).forEach {
                        delay(3000) // 3 seconds
                        repository.insert() // insert dummy data
                    }
                }
            } ?: logger.warning("scope is null")
        }
    }

    override fun stop() {
        logger.info("stop")
        scope?.cancel()
    }

    private fun CoroutineScope.fetchData(repository: RestaurantRepository): ReceiveChannel<Restaurant> = produce {
        repository.getAsFlow().collect {
            logger.info("collect data - ${it.name}")
            send(it)
        }
    }

    private fun CoroutineScope.analyze(channel: ReceiveChannel<Restaurant>) = produce {
        channel.consumeEach { sample ->
            logger.info("analyzing [${sample.name}]")
            scope?.run {
                val result = DefaultAnalyzer(this).analyze(sample.name ?: "")
                logger.info("process pipeline done! [${sample.name}] $result")
                sample.name?.run {
                    send(this)
                }
            }
        }
    }

    private fun CoroutineScope.store(channel: ReceiveChannel<String>) = launch {
        channel.consumeEach { result ->
            logger.info("storing [${result}]")
            scope?.run {
                RestaurantRepository().insert()
            }
        }
    }

    companion object {
        private const val TAG = "SampleProcessor"
        private val logger = Logger.getLogger(TAG)
    }
}