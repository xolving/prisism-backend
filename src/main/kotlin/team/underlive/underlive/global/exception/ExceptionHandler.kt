package team.underlive.underlive.global.exception

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.underlive.underlive.global.exception.error.HttpException
import team.underlive.underlive.global.exception.model.ExceptionResponse

@RestControllerAdvice
class ExceptionHandler {
	private val logger = LoggerFactory.getLogger(javaClass)

	@ExceptionHandler(HttpException::class)
	fun exception(exception: HttpException): ResponseEntity<ExceptionResponse> {
		logger.error("${exception.message} ${exception.statusCode}")
		return ResponseEntity.status(exception.statusCode)
			.body(ExceptionResponse(message = exception.message))
	}
}
