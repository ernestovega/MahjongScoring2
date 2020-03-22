package com.etologic.mahjongscoring2.business.use_cases.games

import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import io.reactivex.Single
import javax.inject.Inject

class DeleteGameUseCase @Inject
constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    fun deleteGame(gameId: Long): Single<List<GameWithRounds>> =
        roundsRepository.deleteByGame(gameId)
            .flatMap { gamesRepository.deleteOne(gameId) }
            .flatMap { gamesRepository.getAllWithRounds() }
}
