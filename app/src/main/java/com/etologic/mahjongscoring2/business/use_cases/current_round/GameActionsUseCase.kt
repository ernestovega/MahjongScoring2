package com.etologic.mahjongscoring2.business.use_cases.current_round

import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.data_source.repositories.CurrentTableRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import com.etologic.mahjongscoring2.data_source.repositories.TablesRepository
import io.reactivex.Single
import javax.inject.Inject

class GameActionsUseCase @Inject
constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tablesRepository: TablesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    internal fun discard(huData: HuData): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                table.finishCurrentRoundByHuDiscard(huData)
                roundsRepository.updateOne(table.rounds.last())
                    .flatMap { createRoundIfLessThanMaxMcrHands(table) }
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }
    
    internal fun selfpick(huData: HuData): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                table.finishCurrentRoundByHuSelfpick(huData)
                roundsRepository.updateOne(table.rounds.last())
                    .flatMap { createRoundIfLessThanMaxMcrHands(table) }
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }
    
    internal fun draw(): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                table.finishCurrentRoundByDraw()
                roundsRepository.updateOne(table.rounds.last())
                    .flatMap { createRoundIfLessThanMaxMcrHands(table) }
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }
    
    internal fun end(): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                if(table.rounds.size == 1) roundsRepository.insertOne(Round(table.game.gameId)).blockingGet()
                roundsRepository.deleteOne(table.game.gameId, table.rounds.last().roundId)
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }
    
    internal fun resume(): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                createRoundIfLessThanMaxMcrHands(table)
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }
    
    internal fun removeRound(roundToRemoveId: Int): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                if (table.rounds.last().isEnded)
                    roundsRepository.insertOne(Round(table.game.gameId)).blockingGet()
                
                roundsRepository.deleteOne(table.game.gameId, roundToRemoveId)
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }
    
    private fun createRoundIfLessThanMaxMcrHands(table: Table): Single<Long> =
        if (table.rounds.size < MAX_MCR_ROUNDS)
            roundsRepository.insertOne(Round(table.game.gameId))
        else
            Single.just(table.game.gameId)
    
    private fun updateCurrentTable(gameId: Long): Single<Table> =
        tablesRepository.getTable(gameId)
            .doOnSuccess { currentTableRepository.set(it) }
}
