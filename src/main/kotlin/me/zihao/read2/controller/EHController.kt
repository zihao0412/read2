package me.zihao.read2.controller

import me.zihao.read2.model.external.ehentai.EHSearchResult
import me.zihao.read2.service.EHGalleryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/eh")
class EHController(private val galleryService: EHGalleryService) {

    @GetMapping("/search")
    fun search(@RequestParam params: Map<String, String>): ResponseEntity<EHSearchResult> =
        ResponseEntity.ok(galleryService.fetchSearchResults(params))
}