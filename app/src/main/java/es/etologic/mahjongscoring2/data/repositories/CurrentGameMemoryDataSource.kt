package es.etologic.mahjongscoring2.data.repositories

import io.reactivex.Single
import java.lang.Boolean.TRUE
import javax.inject.Inject
import javax.inject.Singleton

class CurrentGameMemoryDataSource
@Inject constructor() {
    
    private var currentGameId: Long = -1
    
    internal fun get(): Single<Long> =
        Single.just(currentGameId)
    
    internal fun set(gameId: Long): Single<Boolean> {
        currentGameId = gameId
        return Single.just(TRUE)
    }
    
    internal fun invalidate(): Single<Boolean> {
        currentGameId = -1
        return Single.just(true)
    }
}