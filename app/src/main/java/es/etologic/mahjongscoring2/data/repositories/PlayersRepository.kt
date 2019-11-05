package es.etologic.mahjongscoring2.data.repositories

import android.content.Context
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.PlayersDao
import es.etologic.mahjongscoring2.domain.model.Player
import io.reactivex.Single
import javax.inject.Inject

class PlayersRepository @Inject
constructor(context: Context) : BaseRepository(context) { /*TODO ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/
    private var playersDao: PlayersDao? = null
    
    init {
        playersDao = database?.playersDao
    }
    
    fun insertOne(player: Player): Single<Long> = Single.fromCallable { playersDao?.insert(player) }
    
    fun getOne(playerId: Long): Single<Player> = Single.fromCallable { playersDao?.getOne(playerId) }
    
    fun getOne(playerName: String): Single<Player> = Single.fromCallable { playersDao?.getOne(playerName) }
    
    fun getAllPlayers(): Single<List<Player>> = Single.fromCallable { playersDao?.getAll() ?: ArrayList() }
    
    fun updateOne(player: Player): Single<Boolean> = Single.fromCallable { playersDao?.updateOne(player) == 1 }
    
    fun deleteOne(gameId: Long): Single<Boolean> = Single.fromCallable { playersDao?.deleteOne(gameId) == 1 }
    
    fun deleteAll(): Single<Boolean> = Single.fromCallable { playersDao?.deleteAll() != 0 }
}
