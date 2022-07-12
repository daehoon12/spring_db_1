package hello.jbdc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JbdcApplication

fun main(args: Array<String>) {
	runApplication<JbdcApplication>(*args)
}
