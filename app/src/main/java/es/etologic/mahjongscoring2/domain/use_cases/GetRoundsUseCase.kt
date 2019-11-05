package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.RoundsRepository
import es.etologic.mahjongscoring2.domain.model.Round
import io.reactivex.Single
import javax.inject.Inject

class GetRoundsUseCase @Inject
constructor(private val roundsRepository: RoundsRepository) {
    
    fun getGameRounds(gameId: Long): Single<List<Round>> {
        return roundsRepository.getRoundsByGame(gameId)
    }
}
