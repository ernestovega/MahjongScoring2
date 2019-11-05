package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import es.etologic.mahjongscoring2.domain.model.Round
import io.reactivex.Single
import javax.inject.Inject

class UpdateRoundsUseCase @Inject
constructor(private val roundsRepository: RoundsRepository, private val gamesRepository: GamesRepository) {
    
    fun getGameRounds(gameId: Long): Single<List<Round>> {
        return roundsRepository.getRoundsByGame(gameId)
    }
    
    fun addRound(round: Round): Single<GameWithRounds> = roundsRepository.insertOne(round)
        .flatMap { gamesRepository.getOneWithRounds(round.gameId.toLong()) } //FixMe: ¿ToLong???
}
