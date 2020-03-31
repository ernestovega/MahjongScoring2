package com.etologic.mahjongscoring2.business.use_cases.current_game

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.repositories.CurrentTableRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import io.reactivex.Single
import javax.inject.Inject

class SaveCurrentPlayersUseCase @Inject
constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val gamesRepository: GamesRepository
) {
    
    internal fun saveCurrentGamePlayersNames(names: Array<String>): Single<Table> =
        currentTableRepository.get()
            .flatMap { currentGameWithRounds ->
                currentGameWithRounds.game.nameP1 = names[0]
                currentGameWithRounds.game.nameP2 = names[1]
                currentGameWithRounds.game.nameP3 = names[2]
                currentGameWithRounds.game.nameP4 = names[3]
                
                gamesRepository.updateOne(currentGameWithRounds.game)
                    .flatMap { currentTableRepository.set(currentGameWithRounds) }
                    .map { currentGameWithRounds }
            }
}
