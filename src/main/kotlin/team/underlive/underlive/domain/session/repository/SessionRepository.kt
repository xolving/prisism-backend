package team.underlive.underlive.domain.session.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Repository
import team.underlive.underlive.domain.session.entity.SessionEntity
import java.util.Optional
import java.util.UUID

interface SessionRepository: JpaRepository<SessionEntity, Long> {
	fun findBySocket(socket: UUID): Optional<SessionEntity>

}