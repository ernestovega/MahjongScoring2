package es.etologic.mahjongscoring2.data.repositories

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.PlayersDao
import es.etologic.mahjongscoring2.domain.model.Player
import io.reactivex.Single
import javax.inject.Inject

class PlayersRepository
@Inject constructor() {
    
    @Inject lateinit var playersDao: PlayersDao
    
    fun insertOne(player: Player): Single<Long> =
        playersDao.insert(player)
    
    fun getOne(playerId: Long): Single<Player> =
        playersDao.getOne(playerId)
    
    fun getOne(playerName: String): Single<Player> =
        playersDao.getOne(playerName)
    
    fun getAllPlayers(): Single<List<Player>> =
        playersDao.getAll()
    
    fun updateOne(player: Player): Single<Boolean> =
        playersDao.updateOne(player)
            .map { it == 1 }
    
    fun deleteOne(gameId: Long): Single<Boolean> =
        playersDao.deleteOne(gameId)
            .map { it == 1 }
    
    fun deleteAll(): Single<Boolean> =
        playersDao.deleteAll()
            .map { it != 0 }
}
