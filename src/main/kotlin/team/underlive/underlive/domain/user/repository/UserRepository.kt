package team.underlive.underlive.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.underlive.underlive.domain.user.entity.User
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
	fun existsByEmail(email: String): Boolean
}
