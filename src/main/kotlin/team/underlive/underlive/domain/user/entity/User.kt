package team.underlive.underlive.domain.user.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity(name = "users")
data class User(
	@Id
	@UuidGenerator
	val id: UUID?,
	val email: String,
	val password: String,
	val role: Role,
)
