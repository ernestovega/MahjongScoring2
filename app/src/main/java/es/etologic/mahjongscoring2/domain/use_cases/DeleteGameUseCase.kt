package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import io.reactivex.Single
import javax.inject.Inject

class DeleteGameUseCase @Inject
constructor(private val gamesRepository: GamesRepository) {
    
    fun deleteGame(gameId: Long): Single<Boolean> {
        return gamesRepository.deleteOne(gameId)
    }
}
