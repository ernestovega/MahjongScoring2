package com.etologic.mahjongscoring2.business.use_cases.current_round

import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class DrawUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    internal fun draw(): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                val gameId = currentRound.gameId
                
                currentRound.finishRound()
                
                roundsRepository.updateOne(currentRound)
                    .map { if (gameId < 16) roundsRepository.insertOne(Round(gameId, currentRound.roundId + 1)) }
                    .map { gameId }
            }
            .flatMap(gamesRepository::getOneWithRounds)
            .doOnSuccess { currentGameRepository.set(it) }
}
