package com.etologic.mahjongscoring2.business.use_cases.games

import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.business.model.entities.Game
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class CreateGameUseCase @Inject
constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    internal fun createGame(): Single<Long> =
        gamesRepository.insertOne(Game())
            .flatMap { gameId ->
                roundsRepository.insertOne(Round(gameId))
                    .map { gameId }
            }
}
