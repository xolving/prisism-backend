package team.underlive.underlive.domain.board.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import team.underlive.underlive.domain.board.entity.Board
import team.underlive.underlive.domain.board.entity.dto.CreateBoardRequest
import team.underlive.underlive.domain.board.entity.dto.FetchBoardListResponse
import team.underlive.underlive.domain.board.repository.BoardRepository
import java.time.LocalDateTime
import kotlin.contracts.contract

@Service
class BoardService (
	private val boardRepository: BoardRepository
){
	fun createBoard (createBoardRequest: CreateBoardRequest){
		val board = Board(
			id = 0,
			title = createBoardRequest.title,
			content = createBoardRequest.content,
			createdAt = LocalDateTime.now()
		)

		boardRepository.save(board)
	}

	fun fetchBoardList (): List<FetchBoardListResponse> {
		val boardList = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
		return boardList.map { FetchBoardListResponse(
			id = it.id,
			title = it.title,
			createdAt = it.createdAt
		)}.toList()
	}
}