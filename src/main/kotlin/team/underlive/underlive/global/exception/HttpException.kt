package team.underlive.underlive.global.exception.error

import org.springframework.http.HttpStatus

class HttpException(
	val statusCode: HttpStatus,
	override val message: String,
) : RuntimeException(message) {
	override fun fillInStackTrace(): Throwable {
		return this
	}
}
