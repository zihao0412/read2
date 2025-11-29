package me.zihao.read2.model.external.ehentai

data class EHImageSize(
    val width: Int,
    val height: Int
)

data class EHSlide (
    val pageNumber: Int,
    val totalPageCount: Int,
    val url: String,
    val size: EHImageSize,
    val imageInfo: String,
    val originalImageInfo: String,
    val firstPageLink: String,
    val lastPageLink: String,
    val nextPageLink: String,
    val prevPageLink: String
)