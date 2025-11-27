package me.zihao.read2.service

import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import me.zihao.read2.model.external.ehentai.EHCategory
import me.zihao.read2.model.external.ehentai.EHKey
import me.zihao.read2.model.external.ehentai.EHSearchPage
import me.zihao.read2.model.external.ehentai.EHSearchedGallery
import me.zihao.read2.model.external.ehentai.EHSearchResult
import me.zihao.read2.config.EHProperties
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class EHGalleryService (private val httpClient: HttpClient) {

    fun fetchSearchResults(params: Map<String, String>): EHSearchResult {
        val responseBody: String = runBlocking {
            httpClient.get(EHProperties().baseUrl + "/?page=search") {
                url {
                    params.forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
            }.bodyAsText()
        }
        val document = Jsoup.parse(responseBody)
        return parseEHSearchResult(document)
    }

    private fun parseEHSearchResult(document: Document): EHSearchResult {
        // total count: e.g. "Found 1,441,505 results."
        val totalText = document.selectFirst(".searchtext p")?.text() ?: ""
        val totalCount = Regex("([0-9,]+)").find(totalText)?.value?.replace(",", "")?.toIntOrNull() ?: 0

        // pagination anchors
        val navAnchors = document.select(".searchnav a")
        val hasFirst = navAnchors.any { it.text().contains("First", ignoreCase = true) }
        val hasLast = navAnchors.any { it.text().contains("Last", ignoreCase = true) }
        val hasPrev = navAnchors.any { it.text().contains("Prev", ignoreCase = true) }
        val hasNext = navAnchors.any { it.text().contains("Next", ignoreCase = true) }

        val listedPages = navAnchors.map { a ->
            val href = a.absUrl("href").ifBlank { a.attr("href") }
            EHSearchPage(url = href, isCurrent = false)
        }

        // gallery rows
        val galleryRows = document.select("table.itg.gltc tr:has(.gl3c a)")

        val galleries = galleryRows.map { row ->
            val link = row.selectFirst(".gl3c a")!!
            val title = link.selectFirst(".glink")?.text() ?: link.text()
            val href = link.attr("href")

            val gidTok = Regex("/g/(\\d+)/(\\w+)/").find(href)
            val (gid, token) = if (gidTok != null) {
                gidTok.destructured.toList()
            } else listOf("", "")

            val id = EHKey(path = if (gid.isNotBlank()) "https://e-hentai.org/g/$gid/" else href, key = token)

            val categoryText = row.selectFirst(".gl1c .cn")?.text()?.trim() ?: "Misc"
            val category = EHCategory.values().firstOrNull { it.displayName.equals(categoryText, true) } ?: EHCategory.MISC

            val postedTS = if (gid.isNotBlank()) {
                row.selectFirst("#posted_$gid")?.text() ?: ""
            } else {
                row.selectFirst(".gl2c")?.selectFirst("div")?.text() ?: ""
            }

            val img = row.selectFirst(".gl2c img")
            val cover = img?.attr("data-src")?.takeIf { it.isNotBlank() } ?: img?.attr("src") ?: ""

            val tags = link.select(".gt").mapNotNull { it.attr("title").takeIf { t -> t.isNotBlank() } }

            val uploader = row.selectFirst(".gl4c a")?.text() ?: ""

            val pagesText = row.selectFirst(".gl2c")?.text() ?: ""
            val pageCount = Regex("(\\d+)\\s*pages?").find(pagesText)?.groupValues?.get(1)?.toIntOrNull() ?: 0

            EHSearchedGallery(
                id = id,
                catagory = category,
                postedTS = postedTS,
                title = title,
                coverPictureUrl = cover,
                tags = tags,
                uploadedBy = uploader,
                pageCount = pageCount
            )
        }

        return me.zihao.read2.model.external.ehentai.EHSearchResult(
            totalCount = totalCount,
            hasFirstPage = hasFirst,
            hasLastPage = hasLast,
            hasPrevPage = hasPrev,
            hasNextPage = hasNext,
            listedPages = listedPages,
            galleries = galleries
        )
    }

}

