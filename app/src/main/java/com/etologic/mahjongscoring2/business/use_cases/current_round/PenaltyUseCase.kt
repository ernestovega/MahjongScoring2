package com.etologic.mahjongscoring2.business.use_cases.current_round

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.repositories.CurrentTableRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import com.etologic.mahjongscoring2.data_source.repositories.TablesRepository
import io.reactivex.Single
import javax.inject.Inject

class PenaltyUseCase @Inject
constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tablesRepository: TablesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    fun penalty(penalizedPlayerCurrentSeat: TableWinds, points: Int, isDivided: Boolean): Single<Table> =
        currentTableRepository.get()
            .flatMap { currentTable ->
                val currentRound = currentTable.rounds.last()
                if (isDivided)
                    currentRound.setAllPlayersPenaltyPoints(
                        currentTable.getPlayerInitialSeatByCurrentSeat(penalizedPlayerCurrentSeat, currentTable.rounds.size),
                        points
                    )
                else
                    currentRound.setPlayerPenaltyPoints(
                        currentTable.getPlayerInitialSeatByCurrentSeat(penalizedPlayerCurrentSeat, currentTable.rounds.size),
                        points
                    )
                roundsRepository.updateOne(currentRound)
                    .flatMap { tablesRepository.getTable(currentRound.gameId) }
                    .flatMap { currentTableRepository.set(it) }
            }
    
    internal fun cancelPenalties(): Single<Table> =
        currentTableRepository.get()
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                currentRound.cancelAllPlayersPenalties()
                roundsRepository.updateOne(currentRound)
                    .flatMap { tablesRepository.getTable(currentRound.gameId) }
                    .flatMap { currentTableRepository.set(it) }
            }
}
