package com.etologic.mahjongscoring2.business.use_cases.games

import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.business.model.entities.Game
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class CreateGameUseCase @Inject
constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
    private val currentGameRepository: CurrentGameRepository
) {
    
    internal fun createGame(): Single<Long> =
        gamesRepository.insertOne(Game())
            .flatMap { gameId ->
                val newRound = Round(gameId)
                roundsRepository.insertOne(newRound)
                    .map { gameId }
            }
}
