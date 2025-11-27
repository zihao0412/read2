package me.zihao.read2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class Read2Application

fun main(args: Array<String>) {
	runApplication<Read2Application>(*args)
}
