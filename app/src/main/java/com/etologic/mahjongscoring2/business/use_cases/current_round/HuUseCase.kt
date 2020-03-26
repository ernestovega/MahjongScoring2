package com.etologic.mahjongscoring2.business.use_cases.current_round

import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.MAX_MCR_ROUNDS
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
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                
                currentRound.finishRoundByHuDiscard(
                    currentGameWithRounds.getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, currentRound.roundId),
                    currentGameWithRounds.getPlayerInitialSeatByCurrentSeat(huData.discarderCurrentSeat!!, currentRound.roundId),
                    huData.points
                )
                
                finishCurrentRound(currentRound, currentRound.gameId)
            }
    
    internal fun selfpick(huData: HuData): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                
                currentRound.finishRoundByHuSelfpick(
                    currentGameWithRounds.getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, currentRound.roundId),
                    huData.points
                )
                
                finishCurrentRound(currentRound, currentRound.gameId)
            }
    
    internal fun draw(): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                
                currentRound.finishRound()
                
                finishCurrentRound(currentRound, currentRound.gameId)
            }
    
    private fun finishCurrentRound(currentRound: Round, gameId: Long): Single<GameWithRounds> {
        return roundsRepository.updateOne(currentRound)
            .flatMap {
                if (currentRound.roundId < MAX_MCR_ROUNDS)
                    roundsRepository.insertOne(Round(gameId, currentRound.roundId + 1))
                else
                    Single.just(gameId)
            }
            .flatMap { gamesRepository.getOneWithRounds(gameId) }
            .flatMap { currentGameRepository.set(it) }
    }
}
