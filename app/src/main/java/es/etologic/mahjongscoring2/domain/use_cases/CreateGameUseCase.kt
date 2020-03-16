package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import es.etologic.mahjongscoring2.domain.model.Game
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class CreateGameUseCase @Inject
constructor(private val gamesRepository: GamesRepository) {
    
    internal fun createGame(): Single<Long> {
        val playersNames = ArrayList<String>(4)
        playersNames.add("Player 1")
        playersNames.add("Player 2")
        playersNames.add("Player 3")
        playersNames.add("Player 4")
        val newGame = Game(playersNames)
        return gamesRepository.insertOne(newGame)
    }
}
