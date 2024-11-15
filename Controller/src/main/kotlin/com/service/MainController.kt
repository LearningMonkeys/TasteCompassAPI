package com.service

import com.entity.Restaurant
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class MainController(
    private val dataStorageService: DataStorageService<Restaurant>
) : Controller{
    private var processScope = CoroutineScope(Job() + Dispatchers.Default)
    private val sampleProcessor = SampleProcessor(dataStorageService)

    override fun start() {
        if(!processScope.isActive) {
            processScope = CoroutineScope(Job() + Dispatchers.Default)
        }

        processScope.launch {
            sampleProcessor.start(processScope)
            delay(60 * 1000 * 30) // stop after 30 min
            sampleProcessor.stop()
        }
    }

    override fun stop() {
        processScope.cancel()
    }

    companion object {
        private const val TAG = "SampleProcessor"
        private val logger = Logger.getLogger(TAG)
    }
}