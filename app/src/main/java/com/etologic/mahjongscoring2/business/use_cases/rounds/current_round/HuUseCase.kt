package com.etologic.mahjongscoring2.business.use_cases.rounds.current_round

import com.etologic.mahjongscoring2.app.utils.GameRoundsUtils.Companion.getPlayerInitialSeatByCurrentSeat
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class HuUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    internal fun discard(huData: HuData): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap(gamesRepository::getOneWithRounds)
            .map { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                currentRound.finishRoundByHuDiscard(
                    getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, currentRound.roundId),
                    getPlayerInitialSeatByCurrentSeat(huData.discarderCurrentSeat!!, currentRound.roundId),
                    huData.points
                )
                updateRoundAndCreateNextIfProceed(currentRound)
                currentRound.gameId
            }
            .flatMap(gamesRepository::getOneWithRounds)
    
    internal fun selfpick(huData: HuData): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap(gamesRepository::getOneWithRounds)
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                currentRound.finishRoundByHuSelfpick(
                    getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, currentRound.roundId),
                    huData.points
                )
                updateRoundAndCreateNextIfProceed(currentRound)
                    .map { currentRound.gameId }
            }
            .flatMap(gamesRepository::getOneWithRounds)
    
    private fun updateRoundAndCreateNextIfProceed(currentRound: Round): Single<Long> =
        if (currentRound.gameId >= 16) {
            roundsRepository.updateOne(currentRound)
                .flatMap { roundsRepository.insertOne(Round(currentRound.gameId, currentRound.roundId + 1)) }
                .map { currentRound.gameId }
        } else {
            currentRound.isEnded = true
            roundsRepository.updateOne(currentRound)
                .map { currentRound.gameId }
        }
}
