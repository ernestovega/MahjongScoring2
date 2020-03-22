package com.etologic.mahjongscoring2.business.use_cases.games.current_game

import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import io.reactivex.Single
import javax.inject.Inject

class SetCurrentGameUseCase
@Inject constructor(
    private val currentGameRepository: CurrentGameRepository
) {
    internal fun setCurrentGame(gameId: Long): Single<Boolean> =
        currentGameRepository.set(gameId)
}
