package me.zihao.read2.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ehentai")
data class EHProperties(
    val baseUrl: String = "https://e-hentai.org",
    val galleryUrl: String = "https://e-hentai.org/g/",
    val slideUrl: String = "https://e-hentai.org/s/"      
)