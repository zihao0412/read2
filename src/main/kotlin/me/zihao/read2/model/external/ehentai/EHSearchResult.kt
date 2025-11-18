package me.zihao.read2.model.external.ehentai

data class EHSearchPage(
    val url: String,
    val isCurrent: Boolean
)

data class EHSearchedGallery(
    val id: EHGalleryId,
    val catagory: EHCategory,
    val publishedTS: String,
    val title: String,
    var coverPictureUrl: String,
    var tags: List<String>,
    var uploadedBy: String,
    var pageCount: Int
)

data class EHSearchResult(
    val totalCount: Int,
    val hasFirstPage: Boolean,
    val hasLastPage: Boolean,
    val hasPrevPage: Boolean,
    val hasNextPage: Boolean,
    val listedPages: List<EHSearchPage>
    val galleries: List<EHSearchedGallery>
)