package com.etologic.mahjongscoring2.business.use_cases.current_game

import com.etologic.mahjongscoring2.app.utils.GameRoundsUtils
import com.etologic.mahjongscoring2.app.utils.GameRoundsUtils.Companion
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import io.reactivex.Single
import javax.inject.Inject

class SetCurrentGameUseCase
@Inject constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository
) {
    internal fun setCurrentGame(gameId: Long): Single<GameWithRounds> =
        gamesRepository.getOneWithRounds(gameId)
            .flatMap { currentGameRepository.set(it) }
}
