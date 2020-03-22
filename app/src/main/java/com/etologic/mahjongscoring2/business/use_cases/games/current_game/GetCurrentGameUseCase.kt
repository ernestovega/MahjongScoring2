package com.etologic.mahjongscoring2.business.use_cases.games.current_game

import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.business.model.entities.Game
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class GetCurrentGameUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    internal fun getCurrentGame(): Single<Game> =
        currentGameRepository.get()
            .flatMap {
                if(it >= 0)
                    gamesRepository.getOne(it)
                else
                    Single.error(Throwable())
            }
    
    internal fun getCurrentGameWithRounds(): Single<GameWithRounds> =
        getCurrentGame()
            .flatMap { gamesRepository.getOneWithRounds(it.gameId) }
}
