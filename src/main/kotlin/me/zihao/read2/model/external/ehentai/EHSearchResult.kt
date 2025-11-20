package me.zihao.read2.model.external.ehentai

data class EHSearchPage(
    val url: String,
    val isCurrent: Boolean
)

data class EHSearchedGallery(
    val id: EHKey,
    val catagory: EHCategory,
    val postedTS: String,
    val title: String,
    val coverPictureUrl: String,
    val tags: List<String>,
    val uploadedBy: String,
    val pageCount: Int
)

data class EHSearchResult(
    val totalCount: Int,
    val hasFirstPage: Boolean,
    val hasLastPage: Boolean,
    val hasPrevPage: Boolean,
    val hasNextPage: Boolean,
    val listedPages: List<EHSearchPage>,
    val galleries: List<EHSearchedGallery>
)