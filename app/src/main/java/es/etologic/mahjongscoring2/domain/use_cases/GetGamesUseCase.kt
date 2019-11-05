package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import io.reactivex.Single
import javax.inject.Inject

class GetGamesUseCase @Inject
constructor(private val gamesRepository: GamesRepository) {
    
    fun getAllWithRounds(): Single<List<GameWithRounds>> = gamesRepository.getAllWithRounds()
    
    fun getGame(gameId: Long): Single<GameWithRounds> = gamesRepository.getOneWithRounds(gameId)
}
