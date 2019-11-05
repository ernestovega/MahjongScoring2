package es.etologic.mahjongscoring2.data.repositories

import android.content.Context
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GameWithRoundsDao
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GamesDao
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao
import es.etologic.mahjongscoring2.domain.model.Game
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import io.reactivex.Single
import javax.inject.Inject

class GamesRepository @Inject
constructor(context: Context) : BaseRepository(context) { /*TODO: ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/
    private var gamesDao: GamesDao? = null
    private var roundsDao: RoundsDao? = null
    private var gameWithRoundsDao: GameWithRoundsDao? = null
    
    init {
        database ?: let {
            gamesDao = it.gamesDao
            roundsDao = it.roundsDao
            gameWithRoundsDao = it.gameWithRoundsDao
        }
    }
    
    fun insertOne(game: Game): Single<Long> = Single.fromCallable { gamesDao?.insertOne(game) }
    
    fun updateOne(game: Game): Single<Boolean> = Single.fromCallable { gamesDao?.updateOne(game) == 1 }
    
    fun deleteOne(gameId: Long): Single<Boolean> = Single.fromCallable { roundsDao?.deleteByGame(gameId); gamesDao?.deleteOne(gameId) == 1 }
    
    fun getOneWithRounds(gameId: Long): Single<GameWithRounds> = Single.fromCallable { gameWithRoundsDao?.getGameWithRoundsOrderedByDateDesc(gameId) }
    
    fun getAllWithRounds(): Single<List<GameWithRounds>> = Single.fromCallable { gameWithRoundsDao?.getAllGamesWithRounds() }
}
