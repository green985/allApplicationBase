package com.balbazar.remoteproject

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import com.google.gson.Gson
import java.io.BufferedWriter
import java.util.logging.Logger

class HelloFunction : HttpFunction {
    private val gson = Gson()
    private val logger = Logger.getLogger(HelloFunction::class.java.name)

    override fun service(request: HttpRequest, response: HttpResponse) {
        val writer: BufferedWriter = response.writer
        when (request.method) {
            "GET" -> handleGet(request, writer)
            "POST" -> handlePost(request, writer)
            else -> {
                response.setStatusCode(HTTP_METHOD_NOT_ALLOWED)
                writer.write(gson.toJson(mapOf("error" to "Method not allowed")))
            }
        }
    }

    private fun handleGet(request: HttpRequest, writer: BufferedWriter) {
        val name = request.getFirstQueryParameter("name").orElse("World")
        val responseData = mapOf(
            "message" to "Hello, $name!",
            "timestamp" to System.currentTimeMillis()
        )
        writer.write(gson.toJson(responseData))
    }

    private fun handlePost(request: HttpRequest, writer: BufferedWriter) {
        val requestBody = request.reader.readText()
        val data = try {
            gson.fromJson(requestBody, Map::class.java)
        } catch (e: com.google.gson.JsonSyntaxException) {
            logger.warning("Failed to parse JSON: ${e.message}")
            mapOf<String, Any>()
        }

        val name = data["name"] as? String ?: "World"
        val responseData = mapOf(
            "message" to "Hello from POST, $name!",
            "receivedData" to data
        )
        writer.write(gson.toJson(responseData))
    }

    companion object {
        private const val HTTP_METHOD_NOT_ALLOWED = 405
    }
}
