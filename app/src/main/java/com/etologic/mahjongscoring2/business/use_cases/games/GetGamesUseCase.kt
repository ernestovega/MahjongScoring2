package com.etologic.mahjongscoring2.business.use_cases.games

import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import io.reactivex.Single
import javax.inject.Inject

class GetGamesUseCase @Inject
constructor(private val gamesRepository: GamesRepository) {
    
    fun getAllWithRounds(): Single<List<GameWithRounds>> = gamesRepository.getAllWithRounds()
}
