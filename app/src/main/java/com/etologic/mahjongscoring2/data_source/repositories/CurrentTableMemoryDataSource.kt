package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.business.model.entities.Table
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentTableMemoryDataSource
@Inject constructor() {
    
    private var currentGame: Table? = null
    
    internal fun get(): Single<Table> = Single.just(currentGame)
    
    internal fun set(game: Table): Single<Table> {
        currentGame = game
        return Single.just(currentGame)
    }
}