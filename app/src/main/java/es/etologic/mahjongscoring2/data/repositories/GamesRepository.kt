package es.etologic.mahjongscoring2.data.repositories

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GameWithRoundsDao
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GamesDao
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao
import es.etologic.mahjongscoring2.domain.model.Game
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import io.reactivex.Single
import javax.inject.Inject

class GamesRepository
@Inject constructor() {
    
    @Inject lateinit var gamesDao: GamesDao
    @Inject lateinit var roundsDao: RoundsDao
    @Inject lateinit var gameWithRoundsDao: GameWithRoundsDao
    
    fun insertOne(game: Game): Single<Long> =
        gamesDao.insertOne(game)
    
    fun updateOne(game: Game): Single<Boolean> =
        gamesDao.updateOne(game)
            .map { it == 1 }
    
    fun deleteByGame(gameId: Long): Single<Boolean> =
        roundsDao.deleteByGame(gameId)
            .map { it >= 0 }
    
    fun deleteOne(gameId: Long): Single<Boolean> =
        gamesDao.deleteOne(gameId)
            .map { it == 1 }
    
    fun getOneWithRounds(gameId: Long): Single<GameWithRounds> =
        gameWithRoundsDao.getGameWithRoundsOrderedByDateDesc(gameId)
    
    fun getAllWithRounds(): Single<List<GameWithRounds>> =
        gameWithRoundsDao.getAllGamesWithRounds()
}
