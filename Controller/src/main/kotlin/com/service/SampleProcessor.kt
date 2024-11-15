package com.service

import com.common.Constants
import com.common.Status
import com.entity.Restaurant
import com.entity.RestaurantMetadata
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.logging.Logger

class SampleProcessor(
    private val dataStorageService: DataStorageService<Restaurant>
) : Processor {
    private var scope: CoroutineScope? = null

    override fun start(parentScope: CoroutineScope) {
        logger.info("start")

        scope = scope?.let {
            if (!it.isActive) {
                CoroutineScope(Dispatchers.IO)
            }
            it
        } ?: CoroutineScope(Dispatchers.IO)

        parentScope.launch {
            scope?.launch {
                logger.info("Start pipeline")
                val getData = fetchData()
                val analyzeData = analyze(getData)
                store(analyzeData)

                // Check DB data for debugging
                dataStorageService.getAll().also {
                    logger.info("Total DB count : ${it.size}")
                    it.forEach { restaurant ->
                        logger.info(restaurant.toReadableString())
                    }
                }
                delay(5000)

                // Delete test
                scope?.launch {
                    val idList = listOf("1", "3")
                    dataStorageService.delete(idList)
                    logger.info("deleted records of id 1, 3")
                }
                delay(5000)

                // Check DB data for debugging
                dataStorageService.getAll().also {
                    logger.info("Total DB count : ${it.size}")
                    it.forEach { restaurant ->
                        logger.info(restaurant.toReadableString())
                    }
                }
                delay(5000)

                // Insert test
                scope?.launch {
                    (1 until 6).forEach { idx ->
                        delay(1000)
                        val data = Restaurant(
                            id = idx.toString(),
                            metadata = RestaurantMetadata(
                                id = idx.toString(),
                                status = Status.NEW,
                                name = "Sample Restaurant $idx"
                            ),
                            embedding = null,
                            status = Status.NEW
                        )
                        dataStorageService.insert(listOf(data))
                    }
                }
                delay(12000)

                // Check DB data for debugging
                dataStorageService.getAll().also {
                    logger.info("Total DB count : ${it.size}")
                    it.forEach { restaurant ->
                        logger.info(restaurant.toReadableString())
                    }
                }
                delay(5000)

                // Search test
                scope?.launch {
                    val moodVector = List(Constants.EMBEDDING_SIZE) { 3.0f }
                    val searchResults = dataStorageService.search("mood_vector", 7, listOf(moodVector))
                    logger.info("result search for mood_vector [3.0, 3.0, 3.0, ...]")
                    for (i in searchResults.indices) {
                        val searchResult = searchResults[i]
                        searchResult.forEach { restaurant ->
                            logger.info(restaurant.toReadableString())
                        }
                    }
                }
            } ?: logger.warning("scope is null")
        }
    }

    override fun stop() {
        logger.info("stop")
        scope?.cancel()
    }

    private fun CoroutineScope.fetchData(): ReceiveChannel<Restaurant> = produce {
        dataStorageService.getAsFlow().collect {
            logger.info("collect data - ${it.getMetadata().name}")
            send(it)
        }
    }

    private fun CoroutineScope.analyze(channel: ReceiveChannel<Restaurant>) = produce {
        channel.consumeEach { restaurant ->
            logger.info("analyzing [${restaurant.getMetadata().name}]")
            scope?.run {
                val result = DefaultAnalyzer(this).analyze(restaurant.getMetadata().name ?: "")
                restaurant.getMetadata().mood = result
                logger.info("process pipeline done! [${restaurant.getMetadata().name}] $result")
                restaurant.getMetadata().mood.run {
                    send(restaurant)
                }
            }
        }
    }

    private fun CoroutineScope.store(channel: ReceiveChannel<Restaurant>) = launch {
        channel.consumeEach { restaurant ->
            logger.info("storing [${restaurant.getMetadata().name}]")
            scope?.run {
                dataStorageService.update(listOf(restaurant))
            }
        }
    }

    companion object {
        private const val TAG = "SampleProcessor"
        private val logger = Logger.getLogger(TAG)
    }
}