package team.underlive.underlive.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.underlive.underlive.global.exception.error.HttpException
import team.underlive.underlive.global.exception.model.ExceptionResponse;

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(HttpException::class)
    fun exception(exception: HttpException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity.status(exception.statusCode)
                .body(ExceptionResponse( message = exception.message ));
    }
}