package team.underlive.underlive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class UnderliveApplication

fun main(args: Array<String>) {
	runApplication<UnderliveApplication>(*args)
}
