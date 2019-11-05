package es.etologic.mahjongscoring2.data.repositories

import android.content.Context
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao
import es.etologic.mahjongscoring2.domain.model.Round
import io.reactivex.Single
import javax.inject.Inject

class RoundsRepository @Inject
constructor(context: Context) : BaseRepository(context) { /*TODO ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/
    private var roundsDao: RoundsDao? = null
    
    init {
        roundsDao = database?.roundsDao
    }
    
    fun insertOne(round: Round): Single<Long> = Single.fromCallable { roundsDao?.insertOne(round) }
    
    fun getOne(gameId: Long, roundId: Long): Single<Round> = Single.fromCallable { roundsDao?.getOne(gameId, roundId) }
    
    fun getAll(): Single<List<Round>> = Single.fromCallable { roundsDao?.getAll() }
    
    fun getRoundsByGame(gameId: Long): Single<List<Round>> = Single.fromCallable { roundsDao?.getAllByGame(gameId) }
    
    fun updateOne(round: Round): Single<Boolean> = Single.fromCallable { roundsDao?.updateOne(round) == 1 }
    
    fun deleteOne(gameId: Long, roundId: Long): Single<Boolean> = Single.fromCallable { roundsDao?.deleteOne(gameId, roundId) == 1 }
    
    fun deleteAll(): Single<Boolean> = Single.fromCallable { roundsDao?.deleteAll() != 0 }
}
