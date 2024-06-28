object Dependencies {
	// Default
	const val SPRING_WEB = "org.springframework.boot:spring-boot-starter-web"
	const val JACKSON_KOTLIN = "com.fasterxml.jackson.module:jackson-module-kotlin"
	const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect"
	const val SPRING_TEST = "org.springframework.boot:spring-boot-starter-test"

	// Socket
	const val SPRING_SOCKET = "org.springframework.boot:spring-boot-starter-websocket"

	// Database
	const val SPRING_JPA = "org.springframework.boot:spring-boot-starter-data-jpa"
	const val POSTGRESQL = "org.postgresql:postgresql:${DependencyVersions.POSTGRES_VERSION}"

	// Validation
	const val SPRING_VALIDATION = "org.springframework.boot:spring-boot-starter-validation:${DependencyVersions.VALIDATION_VERSION}"

	// Security
	const val SPRING_SECURITY = "org.springframework.boot:spring-boot-starter-security"
}
