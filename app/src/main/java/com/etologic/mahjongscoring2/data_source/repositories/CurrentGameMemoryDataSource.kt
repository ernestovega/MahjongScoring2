package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import io.reactivex.Single
import java.lang.Boolean.TRUE
import javax.inject.Inject

class CurrentGameMemoryDataSource
@Inject constructor() {
    
    private var currentGame: GameWithRounds? = null
    
    internal fun get(): Single<GameWithRounds> = Single.just(currentGame)
    
    internal fun set(game: GameWithRounds): Single<GameWithRounds> {
        currentGame = game
        return Single.just(currentGame)
    }
    
    internal fun invalidate(): Single<Boolean> {
        currentGame = null
        return Single.just(true)
    }
}