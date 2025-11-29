package me.zihao.read2.controller

import me.zihao.read2.model.external.ehentai.EHGalleryKey
import me.zihao.read2.model.external.ehentai.EHGalleryPage
import me.zihao.read2.model.external.ehentai.EHKey
import me.zihao.read2.model.external.ehentai.EHSearchResult
import me.zihao.read2.model.external.ehentai.EHSlide
import me.zihao.read2.service.EHGalleryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/eh")
class EHController(private val galleryService: EHGalleryService) {

    @GetMapping("/search")
    fun search(@RequestParam params: Map<String, String>): ResponseEntity<EHSearchResult> =
        ResponseEntity.ok(galleryService.fetchSearchResults(params))

    @GetMapping("/g/{path}/{key}")
    fun getGallery(@PathVariable path:String, @PathVariable key:String, @RequestParam(required = false) p:Int?): ResponseEntity<EHGalleryPage> {
        val page = p ?: 0
        val gid = EHGalleryKey(EHKey(path, key), page)
        return ResponseEntity.ok(galleryService.fetchGalleryPage(gid))
    }

    @GetMapping("/s/{path}/{key}")
    fun getSlide(@PathVariable path:String, @PathVariable key:String): ResponseEntity<EHSlide> {
        val sid = EHKey(path, key)
        return ResponseEntity.ok(galleryService.fetchSlidePage(sid))
    }

}