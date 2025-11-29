package me.zihao.read2.service

import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import me.zihao.read2.model.external.ehentai.EHCategory
import me.zihao.read2.model.external.ehentai.EHGalleryPage
import me.zihao.read2.model.external.ehentai.EHKey
import me.zihao.read2.model.external.ehentai.EHGalleryPageLink
import me.zihao.read2.model.external.ehentai.EHTag
import me.zihao.read2.model.external.ehentai.EHSlide
import me.zihao.read2.model.external.ehentai.EHImageSize
import me.zihao.read2.model.external.ehentai.EHSearchPage
import me.zihao.read2.model.external.ehentai.EHSearchedGallery
import me.zihao.read2.model.external.ehentai.EHSearchResult
import me.zihao.read2.config.EHProperties
import me.zihao.read2.model.external.ehentai.EHGalleryKey
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class EHGalleryService (private val httpClient: HttpClient, private val ehProperties: EHProperties) {

    fun fetchSearchResults(params: Map<String, String>): EHSearchResult {
        val responseBody: String = runBlocking {
            httpClient.get(ehProperties.baseUrl + "/?page=search") {
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

    fun fetchGalleryPage(id: EHGalleryKey): EHGalleryPage {
        val galleryUrl = "${ehProperties.baseUrl}/g/${id}"
        val responseBody: String = runBlocking {
            httpClient.get(galleryUrl).bodyAsText()
        }
        val document = Jsoup.parse(responseBody)
        return parseEHGalleryPage(document)
    }

    fun fetchSlidePage(id: EHKey): EHSlide {
        val slideUrl = "${ehProperties.baseUrl}/s/${id}"
        val responseBody: String = runBlocking {
            httpClient.get(slideUrl).bodyAsText()
        }
        val document = Jsoup.parse(responseBody)
        return parseEHSlidePage(document)
    }

    private fun removeURLPrefix(url: String): String {
        val prefix = ehProperties.baseUrl
        var modifiedUrl = url
        if (modifiedUrl.startsWith(prefix)) {
            modifiedUrl = modifiedUrl.removePrefix(prefix)
        }
        return modifiedUrl
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
            EHSearchPage(url = removeURLPrefix(href), isCurrent = false)
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

            val id = EHKey(path = gid, key = token)

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

    private fun parseEHGalleryPage(document: Document): EHGalleryPage {
        // Extract basic info from page structure
        val title = document.selectFirst("#gn")?.text() ?: ""
        val subtitle = document.selectFirst("#gj")?.text() ?: ""

        // Extract category
        val categoryText = document.selectFirst("#gdc .cs")?.text()?.trim() ?: "Misc"
        val category = EHCategory.values().firstOrNull { it.displayName.equals(categoryText, true) } ?: EHCategory.MISC

        // Extract uploader
        val uploader = document.selectFirst("#gdn a")?.text() ?: ""

        // Extract gallery metadata from the table
        var postedTS = ""
        var language = ""
        var fileSize = ""
        var lengthText = "0 pages"

        document.select("#gdd table tr").forEach { row ->
            val label = row.selectFirst(".gdt1")?.text() ?: ""
            val value = row.selectFirst(".gdt2")?.text() ?: ""
            when {
                label.contains("Posted", ignoreCase = true) -> postedTS = value
                label.contains("Language", ignoreCase = true) -> language = value.replace(Regex("\\s*TR\\s*"), "").trim()
                label.contains("File Size", ignoreCase = true) -> fileSize = value
                label.contains("Length", ignoreCase = true) -> lengthText = value
            }
        }

        // Parse page count from lengthText: "55 pages"
        val pageCount = Regex("(\\d+)\\s*pages?").find(lengthText)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        // Extract cover image
        val coverStyle = document.selectFirst("#gleft > div")?.attr("style") ?: ""
        val coverUrl = Regex("url\\(([^)]+)\\)").find(coverStyle)?.groupValues?.get(1) ?: ""

        // Extract tags: from #taglist table rows
        val tags = mutableListOf<EHTag>()
        document.select("#taglist table tr").forEach { row ->
            val namespace = row.selectFirst(".tc")?.text()?.removeSuffix(":") ?: "other"
            row.select("td:last-child .gt a").forEach { a ->
                val tagText = a.text()
                tags.add(EHTag(namespace = namespace, tag = tagText, type = "normal"))
            }
        }

        // Extract page links from pagination
        val pageLinks = mutableListOf<EHGalleryPageLink>()
        document.select(".ptt a, .ptb a").forEachIndexed { index, a ->
            val href = a.attr("href")
            if (href.isNotBlank() && !href.contains("onclick")) {
                val pageNum = a.text().toIntOrNull() ?: (index + 1)
                pageLinks.add(EHGalleryPageLink(pageNumber = pageNum, url = removeURLPrefix(href)))
            }
        }

        // Extract gid/token from page content
        val id = EHKey(path = "", key = "")

        return EHGalleryPage(
            id = id,
            catagory = category,
            postedTS = postedTS,
            title = title,
            subtitle = subtitle,
            coverPictureUrl = coverUrl,
            tags = tags,
            uploadedBy = uploader,
            language = language,
            fileSize = fileSize,
            length = lengthText,
            pageNumber = 1,
            pages = pageLinks
        )
    }

    private fun parseEHSlidePage(document: Document): EHSlide {
        // Current / total pages are inside the .sn div: <div><span>1</span> / <span>55</span></div>
        val counters = document.selectFirst(".sn div")
        val spans = counters?.select("span")
        val pageNumber = spans?.getOrNull(0)?.text()?.toIntOrNull() ?: 1
        val totalCount = spans?.getOrNull(1)?.text()?.toIntOrNull() ?: 1

        // Image element
        val imgEl = document.selectFirst("#img") ?: document.selectFirst("#i3 img")
        val imageUrl = imgEl?.attr("src") ?: ""

        // Image info text, often appears as a sibling div under #i2 or #i4
        val info1 = document.selectFirst("#i2 > div:not(.sn)")?.text()
        val info2 = document.selectFirst("#i4 > div")?.text()
        val imageInfo = info1 ?: info2 ?: ""
        val originalImageInfo = info2 ?: imageInfo

        // Extract size from imageInfo like "0.jpg :: 1280 x 1762 :: 108.0 KiB"
        val sizeMatch = Regex("(\\d+)\\s*[x×]\\s*(\\d+)").find(imageInfo)
        val width = sizeMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val height = sizeMatch?.groupValues?.get(2)?.toIntOrNull() ?: 0

        // Pagination links inside .sn
        val snAnchors = document.select(".sn a")
        val firstLink = snAnchors.firstOrNull()?.attr("href")?.let { removeURLPrefix(it) } ?: ""
        val lastLink = snAnchors.lastOrNull()?.attr("href")?.let { removeURLPrefix(it) } ?: ""
        val prevLink = document.selectFirst(".sn a#prev")?.attr("href")?.let { removeURLPrefix(it) } ?: ""
        val nextLink = document.selectFirst(".sn a#next")?.attr("href")?.let { removeURLPrefix(it) } ?: ""

        return EHSlide(
            pageNumber = pageNumber,
            totalPageCount = totalCount,
            url = imageUrl,
            size = EHImageSize(width = width, height = height),
            imageInfo = imageInfo,
            originalImageInfo = originalImageInfo,
            firstPageLink = firstLink,
            lastPageLink = lastLink,
            nextPageLink = nextLink,
            prevPageLink = prevLink
        )
    }
}

