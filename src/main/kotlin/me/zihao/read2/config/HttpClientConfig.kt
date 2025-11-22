package me.zihao.read2.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.defaultRequest.DefaultRequest
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpClientConfig {

    @Bean
    fun ktorHttpClient(props: AppClientProperties): HttpClient {
        return HttpClient(CIO) {

            install(HttpCookies)

            install(DefaultRequest) {
                headers.append("User-Agent", props.userAgent)
                headers.append("Accept-Language", props.acceptLanguage)
            }

            install(Logging) {
                level = LogLevel.INFO
            }

            engine {
                maxConnectionsCount = props.maxConnections
                requestTimeout = props.timeoutMs
            }
        }
    }
}
