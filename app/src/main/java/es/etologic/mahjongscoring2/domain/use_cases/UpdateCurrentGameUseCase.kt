package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.CurrentGameRepository
import es.etologic.mahjongscoring2.domain.model.Game
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class UpdateCurrentGameUseCase
@Inject constructor(
    private val currentGameRepository: CurrentGameRepository
) {
    internal fun setCurrentGame(gameId: Long): Single<Boolean> =
        currentGameRepository.set(gameId)
}
