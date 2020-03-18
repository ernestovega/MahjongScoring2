package es.etologic.mahjongscoring2.data.repositories

import io.reactivex.Single
import java.lang.Boolean.TRUE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentGameRepository
@Inject constructor(private val memoryDataSource: CurrentGameMemoryDataSource) {
    
    internal fun get(): Single<Long> =
        memoryDataSource.get()
    
    internal fun set(gameId: Long): Single<Boolean> =
        memoryDataSource.set(gameId)
    
    internal fun invalidate(): Single<Boolean> =
        memoryDataSource.invalidate()
}