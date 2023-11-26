/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

    fun discard(huData: HuData): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                table.finishCurrentRoundByHuDiscard(huData)
                roundsRepository.updateOne(table.rounds.last())
                    .flatMap { createRoundIfLessThanMaxMcrHands(table) }
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }

    fun selfPick(huData: HuData): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                table.finishCurrentRoundByHuSelfPick(huData)
                roundsRepository.updateOne(table.rounds.last())
                    .flatMap { createRoundIfLessThanMaxMcrHands(table) }
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }

    fun draw(): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                table.finishCurrentRoundByDraw()
                roundsRepository.updateOne(table.rounds.last())
                    .flatMap { createRoundIfLessThanMaxMcrHands(table) }
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }

    fun end(): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                if (table.rounds.last().areTherePenalties()) {
                    draw()
                } else if (table.rounds.size == 1) {
                    roundsRepository.insertOne(Round(table.game.gameId))
                        .map { table }
                } else {
                    Single.just(table)
                }
            }
            .flatMap { table ->
                roundsRepository.deleteOne(table.game.gameId, table.rounds.last().roundId)
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }

    fun resume(): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                createRoundIfLessThanMaxMcrHands(table)
                    .flatMap { updateCurrentTable(table.game.gameId) }
            }

    fun removeRound(roundToRemoveId: Int): Single<Table> =
        currentTableRepository.get()
            .flatMap { table ->
                if (table.rounds.last().isEnded) {
                    roundsRepository.insertOne(Round(table.game.gameId))
                        .map { table }
                } else {
                    Single.just(table)
                }
            }
            .flatMap { table ->
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
