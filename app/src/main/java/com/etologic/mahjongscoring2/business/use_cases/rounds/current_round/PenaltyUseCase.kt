package com.etologic.mahjongscoring2.business.use_cases.rounds.current_round

import com.etologic.mahjongscoring2.app.utils.GameRoundsUtils.Companion.getPlayerInitialSeatByCurrentSeat
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class PenaltyUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    fun penalty(penalizedPlayerCurrentSeat: TableWinds, points: Int, isDivided: Boolean): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap(gamesRepository::getOneWithRounds)
            .map { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                if (isDivided)
                    currentRound.setAllPlayersPenaltyPoints(
                        getPlayerInitialSeatByCurrentSeat(penalizedPlayerCurrentSeat, currentRound.roundId),
                        points
                    )
                else
                    currentRound.setPlayerPenaltyPoints(
                        getPlayerInitialSeatByCurrentSeat(penalizedPlayerCurrentSeat, currentRound.roundId),
                        points
                    )
                roundsRepository.updateOne(currentRound)
                currentGameWithRounds
            }
}
