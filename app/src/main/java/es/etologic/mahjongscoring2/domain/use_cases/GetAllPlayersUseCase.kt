package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository
import es.etologic.mahjongscoring2.domain.model.Player
import io.reactivex.Single
import javax.inject.Inject

class GetAllPlayersUseCase @Inject
constructor(private val playersRepository: PlayersRepository) {
    
    val all: Single<List<Player>>
        get() = playersRepository.getAllPlayers()
}
