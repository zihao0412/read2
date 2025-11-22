package me.zihao.read2.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.client")
data class AppClientProperties(
    var followRedirects: Boolean = true,
    var timeoutMs: Long = 5000,
    var userAgent: String = "MyKtorClient/1.0"
)
