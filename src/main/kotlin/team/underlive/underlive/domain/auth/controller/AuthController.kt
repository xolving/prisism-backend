package team.underlive.underlive.domain.auth.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.underlive.underlive.domain.auth.entity.dto.request.EmailExistsRequest
import team.underlive.underlive.domain.auth.entity.dto.request.UserJoinRequest
import team.underlive.underlive.domain.auth.entity.dto.request.UserLoginRequest
import team.underlive.underlive.domain.auth.entity.dto.response.EmailExistsResponse
import team.underlive.underlive.domain.auth.entity.dto.response.UserLoginResponse
import team.underlive.underlive.domain.auth.service.AuthService

@RestController
@RequestMapping("/auth")
class AuthController(
	private val authService: AuthService,
) {
	@PostMapping("/join")
	fun createUser(
		@RequestBody @Valid userJoinRequest: UserJoinRequest,
	): ResponseEntity<Void> {
		authService.createUser(userJoinRequest)
		return ResponseEntity.ok().build()
	}

	@PostMapping("/login")
	fun login(
		@RequestBody @Valid userLoginRequest: UserLoginRequest,
	): ResponseEntity<UserLoginResponse> {
		return ResponseEntity.ok(authService.loginUser(userLoginRequest))
	}

	@PostMapping("/exists")
	fun isEmailExists(
		@RequestBody @Valid emailExistsRequest: EmailExistsRequest,
	): ResponseEntity<EmailExistsResponse> {
		return ResponseEntity.ok(authService.isEmailExists(emailExistsRequest))
	}
}
