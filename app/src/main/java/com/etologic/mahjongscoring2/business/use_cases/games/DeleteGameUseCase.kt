package com.etologic.mahjongscoring2.business.use_cases.games

import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import io.reactivex.Single
import javax.inject.Inject

class DeleteGameUseCase @Inject
constructor(private val gamesRepository: GamesRepository) {
    
    fun deleteGame(gameId: Long): Single<List<GameWithRounds>> {
        return gamesRepository.deleteOne(gameId)
            .flatMap { gamesRepository.getAllWithRounds() }
    }
}
