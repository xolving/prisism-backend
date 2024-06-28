package team.underlive.underlive.domain.auth.service

import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import team.underlive.underlive.domain.auth.entity.dto.request.EmailExistsRequest
import team.underlive.underlive.domain.auth.entity.dto.request.UserJoinRequest
import team.underlive.underlive.domain.auth.entity.dto.request.UserLoginRequest
import team.underlive.underlive.domain.auth.entity.dto.response.EmailExistsResponse
import team.underlive.underlive.domain.auth.entity.dto.response.UserLoginResponse
import team.underlive.underlive.domain.user.entity.Role
import team.underlive.underlive.domain.user.entity.User
import team.underlive.underlive.domain.user.repository.UserRepository
import team.underlive.underlive.global.exception.error.HttpException

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
) {
	fun createUser(userJoinRequest: UserJoinRequest) {
		if (userRepository.existsByEmail(userJoinRequest.email)) {
			throw HttpException(HttpStatus.BAD_REQUEST, "이미 해당 이메일로 가입된 계정이 존재합니다.")
		}

		val encodedPassword = passwordEncoder.encode(userJoinRequest.password)
		val user =
			User(
				id = null,
				email = userJoinRequest.email,
				password = encodedPassword,
				role = Role.ROLE_USER,
			)

		userRepository.save(user)
	}

	fun loginUser(userLoginRequest: UserLoginRequest): UserLoginResponse {
		val user =
			userRepository.findByEmail(userLoginRequest.email)
				.orElseThrow { HttpException(HttpStatus.NOT_FOUND, "해당 이메일을 사용하는 유저를 찾을 수 없습니다.") }

		if (!passwordEncoder.matches(userLoginRequest.password, user.password))
			{
				throw HttpException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.")
			}

		return UserLoginResponse("test", "test")
	}

	fun isEmailExists(emailExistsRequest: EmailExistsRequest): EmailExistsResponse {
		if (userRepository.existsByEmail(emailExistsRequest.email)) {
			return EmailExistsResponse(true)
		}

		return EmailExistsResponse(false)
	}
}
