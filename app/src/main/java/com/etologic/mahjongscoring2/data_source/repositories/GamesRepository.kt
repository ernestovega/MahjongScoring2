package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.business.model.entities.Game
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.TableDao
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesRepository
@Inject constructor() {
    
    @Inject
    lateinit var gamesDao: GamesDao
    
    @Inject
    lateinit var roundsDao: RoundsDao
    
    @Inject
    lateinit var tableDao: TableDao
    
    internal fun insertOne(game: Game): Single<Long> =
        gamesDao.insertOne(game)
    
    internal fun updateOne(game: Game): Single<Boolean> =
        gamesDao.updateOne(game)
            .map { it == 1 }
    
    internal fun deleteOne(gameId: Long): Single<Boolean> =
        gamesDao.deleteOne(gameId)
            .map { it == 1 }
}
