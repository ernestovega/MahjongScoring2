package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.CurrentGameRepository
import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class EndGameUseCase @Inject
constructor(
    private val gamesRepository: GamesRepository,
    private val currentGameRepository: CurrentGameRepository
) {
    
    internal fun endGame(gameId: Long): Single<Boolean> =
        gamesRepository.getOne(gameId)
            .flatMap {
                it.endDate = Date()
                gamesRepository.updateOne(it)
            }
            .flatMap { currentGameRepository.invalidate() }
}
