package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.RoundsDao
import com.etologic.mahjongscoring2.business.model.entities.Round
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoundsRepository
@Inject constructor() {
    
    @Inject lateinit var roundsDao: RoundsDao
    
    fun insertOne(round: Round) =
        roundsDao.insertOne(round)
    
    fun getOne(gameId: Long, roundId: Long) =
        roundsDao.getOne(gameId, roundId)
    
    fun getAll() =
        roundsDao.getAll()
    
    fun getRoundsByGame(gameId: Long) =
        roundsDao.getAllByGame(gameId)
    
    fun updateOne(round: Round) =
        roundsDao.updateOne(round)
            .map { it == 1 }
    
    fun deleteOne(gameId: Long, roundId: Long) =
        roundsDao.deleteOne(gameId, roundId)
            .map { it == 1 }
    
    fun deleteAll() =
        roundsDao.deleteAll()
            .map { it != 0 }
}
