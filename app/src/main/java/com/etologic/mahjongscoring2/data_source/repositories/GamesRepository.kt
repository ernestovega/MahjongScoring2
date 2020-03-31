package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.GameWithRoundsDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.RoundsDao
import com.etologic.mahjongscoring2.business.model.entities.Game
import com.etologic.mahjongscoring2.business.model.entities.Table
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesRepository
@Inject constructor() {
    
    @Inject lateinit var gamesDao: GamesDao
    @Inject lateinit var roundsDao: RoundsDao
    @Inject lateinit var gameWithRoundsDao: GameWithRoundsDao
    
    internal fun insertOne(game: Game): Single<Long> =
        gamesDao.insertOne(game)
    
    internal fun updateOne(game: Game): Single<Boolean> =
        gamesDao.updateOne(game)
            .map { it == 1 }
    
    internal fun deleteOne(gameId: Long): Single<Boolean> =
        gamesDao.deleteOne(gameId)
            .map { it == 1 }
    
    internal fun getOneWithRounds(gameId: Long): Single<Table> =
        gameWithRoundsDao.getGameWithRoundsOrderedByDateDesc(gameId)
    
    internal fun getAllWithRounds(): Single<List<Table>> =
        gameWithRoundsDao.getAllGamesWithRounds()
}
