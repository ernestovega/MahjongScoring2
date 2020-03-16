package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import es.etologic.mahjongscoring2.domain.model.Game
import io.reactivex.Single
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import javax.inject.Inject

class UpdateGameUseCase @Inject
constructor(private val gamesRepository: GamesRepository) {
    
    fun updateGame(game: Game): Single<Boolean> = gamesRepository.updateOne(game)
}
