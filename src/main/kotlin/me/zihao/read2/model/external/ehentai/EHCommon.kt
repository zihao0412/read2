package me.zihao.read2.model.external.ehentai

enum class EHCategory(
    val code: String,
    val displayName: String
) {
    DOUJINSHI("2", "Doujinshi"),
    MANGA("4", "Manga"),
    ARTIST_CG("8", "Artist CG"),
    GAME_CG("16", "Game CG"),
    WESTERN("512", "Western"),
    NON_H("256", "Non-H"),
    IMAGE_SET("32", "Image Set"),
    COSPLAY("64", "Cosplay"),
    ASIAN_PORN("128", "Asian Porn"),
    MISC("1", "Misc")
}

data class EHKey(
    val path: String,
    val key: String) {
    override fun toString(): String = "/$path/$key"
}

data class EHGalleryKey(
    val gid: EHKey,
    val page: Int){
    override fun toString(): String {
        if (page > 1) {
            return "${gid}/?p=${page - 1}"
        } else {
            return gid.toString()
        }
    }
    }

data class EHTag(
    val namespace: String,
    val tag: String,
    val type: String
)

