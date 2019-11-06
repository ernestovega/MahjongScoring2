package es.etologic.mahjongscoring2.data.repositories

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao
import es.etologic.mahjongscoring2.domain.model.Round
import javax.inject.Inject

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
