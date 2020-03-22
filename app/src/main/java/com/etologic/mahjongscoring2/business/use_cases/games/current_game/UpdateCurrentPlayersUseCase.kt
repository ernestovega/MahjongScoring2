package com.etologic.mahjongscoring2.business.use_cases.games.current_game

import com.etologic.mahjongscoring2.business.model.entities.Game
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import io.reactivex.Single
import javax.inject.Inject

class UpdateCurrentPlayersUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository
) {
    
    internal fun saveCurrentGamePlayersNames(names: Array<String>): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap(gamesRepository::getOneWithRounds)
            .map { currentGameWithRounds ->
                currentGameWithRounds.game.nameP1 = names[0]
                currentGameWithRounds.game.nameP2 = names[1]
                currentGameWithRounds.game.nameP3 = names[2]
                currentGameWithRounds.game.nameP4 = names[3]
                gamesRepository.updateOne(currentGameWithRounds.game)
                currentGameWithRounds
            }
}
