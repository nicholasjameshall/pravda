package com.ug.pravda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.hateoas.config.EnableHypermediaSupport

@SpringBootApplication
@EnableHypermediaSupport(type = [
	EnableHypermediaSupport.HypermediaType.HAL,
	EnableHypermediaSupport.HypermediaType.HAL_FORMS,
])
class PravdaApplication

fun main(args: Array<String>) {
	runApplication<PravdaApplication>(*args)
}
