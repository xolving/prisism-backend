package team.underlive.underlive.domain.board.service

import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import team.underlive.underlive.domain.board.entity.Board
import team.underlive.underlive.domain.board.entity.dto.CreateBoardRequest
import team.underlive.underlive.domain.board.entity.dto.FetchBoardListResponse
import team.underlive.underlive.domain.board.entity.dto.FetchBoardResponse
import team.underlive.underlive.domain.board.repository.BoardRepository
import team.underlive.underlive.global.exception.error.HttpException
import java.time.LocalDateTime

@Service
class BoardService(
	private val boardRepository: BoardRepository,
) {
	fun createBoard(createBoardRequest: CreateBoardRequest) {
		val board =
			Board(
				id = 0,
				title = createBoardRequest.title,
				content = createBoardRequest.content,
				createdAt = LocalDateTime.now(),
			)

		boardRepository.save(board)
	}

	fun fetchBoardList(): List<FetchBoardListResponse> {
		val boardList = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
		return boardList.map {
			FetchBoardListResponse(
				id = it.id,
				title = it.title,
				createdAt = it.createdAt,
			)
		}.toList()
	}

	fun fetchBoard(id: Long): FetchBoardResponse {
		val board =
			boardRepository.findById(id).orElseThrow {
				HttpException(HttpStatus.NOT_FOUND, "해당하는 게시글을 찾을 수 없습니다.")
			}

		return FetchBoardResponse(
			id = board.id,
			title = board.title,
			content = board.content,
			createdAt = board.createdAt,
		)
	}
}
