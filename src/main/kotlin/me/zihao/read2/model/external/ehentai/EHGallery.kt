package me.zihao.read2.model.external.ehentai

data class EHGalleryPage (
    val id: EHKey,
    val catagory: EHCategory,
    val postedTS: String,
    val title: String,
    val subtitle: String,
    val coverPictureUrl: String,
    val tags: List<EHTag>,
    val uploadedBy: String,
    val language: String,
    val fileSize: String,
    val length: String,
    val pageNumber: Int,
    val pages: List<EHGalleryPageLink>
)

data class EHGalleryPageLink (
    val pageNumber: Int,
    val url: String
)

data class EHGallerySlide (
    val pageNumber: Int,
    val id: EHKey,
    val thumbUrl: String
)