package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import es.etologic.mahjongscoring2.domain.model.Game
import io.reactivex.Single
import javax.inject.Inject

class UpdateGameUseCase @Inject
constructor(private val gamesRepository: GamesRepository) {
    
    fun updateGame(game: Game): Single<Boolean> {
        return gamesRepository.updateOne(game)
            .flatMap { wasUpdated ->
                if (wasUpdated)
                    gamesRepository.updateOne(game)
                        .flatMap { Single.just(java.lang.Boolean.TRUE) }
                else
                    gamesRepository.updateOne(game)
                        .flatMap { Single.error<Boolean?>(Exception()) }
            }
    }
}
