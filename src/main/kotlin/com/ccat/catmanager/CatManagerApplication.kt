package com.ccat.catmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CatManagerApplication

fun main(args: Array<String>) {
	runApplication<CatManagerApplication>(*args)
}
