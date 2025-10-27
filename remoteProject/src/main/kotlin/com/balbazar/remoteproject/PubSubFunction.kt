package com.balbazar.remoteproject

import com.google.cloud.functions.BackgroundFunction
import com.google.cloud.functions.Context
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.logging.Logger

data class PubSubMessage(
    val data: String,
    val attributes: Map<String, String>? = null,
    val messageId: String? = null,
    val publishTime: String? = null,
)

class PubSubFunction : BackgroundFunction<PubSubMessage> {
    private val logger = Logger.getLogger(PubSubFunction::class.java.name)

    override fun accept(message: PubSubMessage, context: Context) {
        val decodedData = String(
            Base64.getDecoder().decode(message.data),
            StandardCharsets.UTF_8
        )

        logger.info("Message ID: ${context.eventId()}")
        logger.info("Event Type: ${context.eventType()}")
        logger.info("Timestamp: ${context.timestamp()}")
        logger.info("Decoded Data: $decodedData")

        message.attributes?.let { attrs ->
            logger.info("Attributes: $attrs")
        }

        processMessage(decodedData)
    }

    private fun processMessage(data: String) {
        logger.info("Processing message: $data")
    }
}
