package me.zihao.read2.model.external.ehentai

enum class EHCategory(
    val code: String,
    val displayName: String
) {
    ALL("0", "All"),
    ARTIST_CG("1", "Artist CG"),
    COSPLAY("2", "Cosplay"),
    GAME_CG("3", "Game CG"),
    MANGA("4", "Manga"),
    NON_H("5", "Non-H"),
    WESTERN("6", "Western")
}