package team.underlive.underlive.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {
	@Bean
	@Throws(Exception::class)
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
		http.cors { corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()) }

		http.sessionManagement { sessionManagement: SessionManagementConfigurer<HttpSecurity?> ->
			sessionManagement.sessionCreationPolicy(
				SessionCreationPolicy.STATELESS
			)
		}

		http.authorizeHttpRequests { authorizeHttpRequests ->
			authorizeHttpRequests
				.anyRequest().permitAll()
		}

		return http.build()
	}

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		val config = CorsConfiguration()

		config.allowCredentials = true
		config.allowedOrigins = mutableListOf("http://localhost:3000", "https://www.prisism.com")
		config.allowedMethods = mutableListOf("*")
		config.allowedHeaders = mutableListOf("*")
		config.exposedHeaders = mutableListOf("*")
		config.maxAge = 86400L

		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", config)
		return source
	}
}