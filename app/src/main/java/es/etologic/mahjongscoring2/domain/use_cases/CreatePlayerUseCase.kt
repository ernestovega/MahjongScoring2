package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository
import es.etologic.mahjongscoring2.domain.model.Player
import io.reactivex.Single
import javax.inject.Inject

class CreatePlayerUseCase @Inject
constructor(private val playersRepository: PlayersRepository) {
    
    fun createPlayer(playerName: String): Single<Long> {
        return playersRepository.insertOne(Player.getNewPlayer(playerName))
    }
}
