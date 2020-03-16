package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.CurrentGameRepository
import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import io.reactivex.Single
import javax.inject.Inject

class GetCurrentGameUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository,
    private val gamesRepository: GamesRepository
) {
    
    internal fun getCurrentGameWithRounds(): Single<GameWithRounds> =
        currentGameRepository.get()
            .flatMap {
                if(it >= 0)
                    gamesRepository.getOneWithRounds(it)
                else
                    Single.error(Throwable())
            }
}
