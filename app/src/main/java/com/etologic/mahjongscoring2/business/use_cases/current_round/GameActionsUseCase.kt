package com.etologic.mahjongscoring2.business.use_cases.current_round

import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class GameActionsUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    internal fun discard(huData: HuData): Single<Table> =
        currentGameRepository.get()
            .flatMap {
                it.finishCurrentRoundByHuDiscard(huData)
                finishCurrentRound(it.rounds.last())
            }
    
    internal fun selfpick(huData: HuData): Single<Table> =
        currentGameRepository.get()
            .flatMap {
                it.finishCurrentRoundByHuSelfpick(huData)
                finishCurrentRound(it.rounds.last())
            }
    
    internal fun draw(): Single<Table> =
        currentGameRepository.get()
            .flatMap {
                it.finishCurrentRoundByDraw()
                finishCurrentRound(it.rounds.last())
            }
    
    private fun finishCurrentRound(currentRound: Round): Single<Table> =
        roundsRepository.updateOne(currentRound)
            .flatMap {
                if (currentRound.roundId < MAX_MCR_ROUNDS)
                    roundsRepository.insertOne(Round(currentRound.gameId, currentRound.roundId + 1))
                else
                    Single.just(currentRound.gameId)
            }
            .flatMap { gamesRepository.getOneWithRounds(currentRound.gameId) }
            .doOnSuccess { currentGameRepository.set(it) }
    
    internal fun end(): Single<Table> =
        currentGameRepository.get()
            .flatMap { table ->
                val currentRound = table.rounds.last()
            
                currentRound.endRound()
    
                roundsRepository.updateOne(currentRound)
                    .flatMap { gamesRepository.getOneWithRounds(currentRound.gameId) }
                    .doOnSuccess { currentGameRepository.set(it) }
            }
    
    internal fun resume(): Single<Table> =
        currentGameRepository.get()
            .flatMap { table ->
                val currentRound = table.rounds.last()
            
                currentRound.resumeRound()
            
                roundsRepository.updateOne(currentRound)
                    .flatMap { gamesRepository.getOneWithRounds(currentRound.gameId) }
                    .doOnSuccess { currentGameRepository.set(it) }
            }
    
    internal fun removeRound(roundId: Int): Single<Table> =
        currentGameRepository.get()
            .flatMap { table ->
                roundsRepository.deleteOne(table.game.gameId, roundId)
                    .flatMap { gamesRepository.getOneWithRounds(table.game.gameId) }
                    .map { table.resetTotals() }
                    .map { table2 -> table2.rounds.forEach { roundsRepository.updateOne(it).blockingGet() } }
                    .flatMap { gamesRepository.getOneWithRounds(table.game.gameId) }
                    .doOnSuccess { currentGameRepository.set(it) }
            }
}
