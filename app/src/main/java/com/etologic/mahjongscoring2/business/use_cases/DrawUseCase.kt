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
package com.etologic.mahjongscoring2.business.use_cases

import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.model.entities.applyPenaltiesAndMarkRoundAsEnded
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class DrawUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
    private val resumeGameUseCase: ResumeGameUseCase,
) {
    operator fun invoke(gameId: GameId): Single<UIGame> =
        roundsRepository.getAllByGame(gameId)
            .flatMap { gameRounds ->
                val currentRound = gameRounds.last()
                currentRound.finishByDraw()
                roundsRepository.updateOne(currentRound)
                    .flatMap { resumeGameUseCase(gameId, gameRounds.size) }
            }

    private fun Round.finishByDraw() {
        this.applyPenaltiesAndMarkRoundAsEnded()
    }
}
