package com.etologic.mahjongscoring2.testutil

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.games.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.RoundsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * In-memory implementations of the repository contracts, mirroring the observable behaviour of the
 * Room DAOs (auto-increment ids starting at 1, ordering, return values). They let the business layer
 * be tested on a plain JVM, which is what a KMP `commonTest` will be able to run as well.
 */
class FakeGamesRepository : GamesRepository {

    private var nextId: GameId = 1
    val games = MutableStateFlow<List<DbGame>>(emptyList())

    /** When not null, every write operation fails with this exception. */
    var failure: Throwable? = null

    override fun getAllFlow(): Flow<List<DbGame>> =
        games.map { list -> list.sortedWith(compareByDescending<DbGame> { it.startDate }.thenByDescending { it.gameId }) }

    override fun getOneFlow(gameId: GameId): Flow<DbGame> =
        games.map { list -> list.firstOrNull { it.gameId == gameId } }.filterNotNull()

    override suspend fun getOne(gameId: GameId): Result<DbGame> =
        runCatching { games.value.first { it.gameId == gameId } }

    override suspend fun insertOne(dbGame: DbGame): Result<GameId> = runCatching {
        failure?.let { throw it }
        val id = if (dbGame.gameId == 0L) nextId++ else dbGame.gameId.also { nextId = maxOf(nextId, it + 1) }
        require(games.value.none { it.gameId == id }) { "UNIQUE constraint failed: Games.gameId" }
        games.value = games.value + dbGame.copy(gameId = id)
        id
    }

    override suspend fun updateOne(dbGame: DbGame): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = games.value.any { it.gameId == dbGame.gameId }
        if (exists) games.value = games.value.map { if (it.gameId == dbGame.gameId) dbGame.copy() else it }
        exists
    }

    override suspend fun deleteOne(gameId: GameId): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = games.value.any { it.gameId == gameId }
        games.value = games.value.filterNot { it.gameId == gameId }
        exists
    }
}

class FakeRoundsRepository : RoundsRepository {

    private var nextId: RoundId = 1

    /**
     * DbRound is a data class whose equals() only uses (gameId, roundId), so a StateFlow of the plain list
     * would swallow updates. Each write publishes a new [Snapshot] (identity equality) instead.
     */
    private class Snapshot(val list: List<DbRound>)

    private val state = MutableStateFlow(Snapshot(emptyList()))

    inner class Store {
        var value: List<DbRound>
            get() = state.value.list
            set(newValue) {
                state.value = Snapshot(newValue)
            }
    }

    val rounds = Store()

    var failure: Throwable? = null

    override fun getAllFlow(): Flow<List<DbRound>> = state.map { snapshot -> snapshot.list.map { it.clone() } }

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>> =
        state.map { snapshot -> snapshot.list.filter { it.gameId == gameId }.map { it.clone() } }

    override suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>> =
        runCatching { rounds.value.filter { it.gameId == gameId }.sortedBy { it.roundId }.map { it.clone() } }

    override suspend fun getOne(roundId: RoundId): Result<DbRound> =
        runCatching { rounds.value.first { it.roundId == roundId }.clone() }

    override suspend fun insertOne(dbRound: DbRound): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val id = if (dbRound.roundId == 0L) nextId++ else dbRound.roundId.also { nextId = maxOf(nextId, it + 1) }
        rounds.value = rounds.value + dbRound.clone(roundId = id)
        id > 0
    }

    override suspend fun updateOne(dbRound: DbRound): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = rounds.value.any { it.roundId == dbRound.roundId }
        if (exists) rounds.value = rounds.value.map { if (it.roundId == dbRound.roundId) dbRound.clone() else it }
        exists
    }

    override suspend fun deleteGameRounds(gameId: GameId): Result<Boolean> = runCatching {
        failure?.let { throw it }
        rounds.value = rounds.value.filterNot { it.gameId == gameId }
        true
    }

    override suspend fun deleteOne(roundId: RoundId): Result<Boolean> = runCatching {
        failure?.let { throw it }
        val exists = rounds.value.any { it.roundId == roundId }
        rounds.value = rounds.value.filterNot { it.roundId == roundId }
        exists
    }
}

fun DbRound.clone(roundId: RoundId = this.roundId): DbRound =
    DbRound(
        gameId = gameId,
        roundId = roundId,
        winnerInitialSeat = winnerInitialSeat,
        discarderInitialSeat = discarderInitialSeat,
        handPoints = handPoints,
        penaltyP1 = penaltyP1,
        penaltyP2 = penaltyP2,
        penaltyP3 = penaltyP3,
        penaltyP4 = penaltyP4,
    )
