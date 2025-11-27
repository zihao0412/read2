package me.zihao.read2.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import java.io.File

class EHGalleryServiceTest {

    @Test
    fun testDefaultSearchResult() = runTest {
        val htmlContent = loadTestHtml("default_search.html")
        val mockClient = createMockHttpClient(htmlContent)
        val service = EHGalleryService(mockClient)

        val result = service.fetchSearchResults(emptyMap())

        assertNotNull(result)
        assertEquals(1441505, result.totalCount)
        assertTrue(result.galleries.isNotEmpty())
        assertTrue(result.hasNextPage)
    }

    private fun loadTestHtml(filename: String): String {
        val resource = javaClass.classLoader.getResourceAsStream("html/$filename")
        return resource?.bufferedReader()?.use { it.readText() } ?: ""
    }

    private fun createMockHttpClient(htmlContent: String): HttpClient {
        val mockEngine = MockEngine { request ->
            respondOk(htmlContent)
        }
        return HttpClient(mockEngine)
    }
}
