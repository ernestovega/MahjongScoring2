package com.etologic.mahjongscoring2.business.use_cases.rounds.current_round

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
    companion object {
        private const val MAX_MCR_HANDS_PER_GAME = 16
    }
    
    internal fun draw(): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap(gamesRepository::getOneWithRounds)
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                currentRound.finishRound()
                updateRoundAndCreateNextIfProceed(currentRound)
                    .map { currentRound.gameId }
            }
            .flatMap(gamesRepository::getOneWithRounds)
    
    private fun updateRoundAndCreateNextIfProceed(currentRound: Round): Single<Long> =
        roundsRepository.updateOne(currentRound)
            .flatMap {
                if (currentRound.gameId < MAX_MCR_HANDS_PER_GAME)
                    roundsRepository.insertOne(Round(currentRound.gameId, currentRound.roundId + 1))
                        .map { currentRound.gameId }
                else
                    Single.just(currentRound.gameId)
            }
}
