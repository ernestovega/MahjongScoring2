package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.RoundsDao
import com.etologic.mahjongscoring2.business.model.entities.Round
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoundsRepository
@Inject constructor() {
    
    @Inject lateinit var roundsDao: RoundsDao
    
    internal fun insertOne(round: Round) =
        roundsDao.insertOne(round)
    
    internal fun getOne(gameId: Long, roundId: Int) =
        roundsDao.getOne(gameId, roundId)
    
    internal fun getAll() =
        roundsDao.getAll()
    
    internal fun getRoundsByGame(gameId: Long) =
        roundsDao.getAllByGame(gameId)
    
    internal fun updateOne(round: Round) =
        roundsDao.updateOne(round)
            .map { it == 1 }
    
    internal fun deleteOne(gameId: Long, roundId: Int) =
        roundsDao.deleteOne(gameId, roundId)
            .map { it == 1 }
    
    internal fun deleteByGame(gameId: Long): Single<Boolean> =
        roundsDao.deleteByGame(gameId)
            .map { it >= 0 }
    
    internal fun deleteAll() =
        roundsDao.deleteAll()
            .map { it != 0 }
}
