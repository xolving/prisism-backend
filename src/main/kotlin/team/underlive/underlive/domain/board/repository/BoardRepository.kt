package team.underlive.underlive.domain.board.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.underlive.underlive.domain.board.entity.Board

interface BoardRepository: JpaRepository<Board, Long> {
}