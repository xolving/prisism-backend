package team.underlive.underlive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UnderliveApplication

fun main(args: Array<String>) {
	runApplication<UnderliveApplication>(*args)
}
