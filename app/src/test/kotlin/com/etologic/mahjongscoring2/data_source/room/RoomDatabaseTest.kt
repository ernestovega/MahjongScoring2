package com.etologic.mahjongscoring2.data_source.room

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.use_cases.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.games.DefaultGamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.DefaultRoundsRepository
import com.etologic.mahjongscoring2.testutil.GameReport
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.etologic.mahjongscoring2.testutil.Goldens
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/** Room DAOs and repositories on a real (in-memory) SQLite database, run on the JVM with Robolectric. */
@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var games: DefaultGamesRepository
    private lateinit var rounds: DefaultRoundsRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        games = DefaultGamesRepository(db.gamesDao)
        rounds = DefaultRoundsRepository(db.roundsDao)
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `ids are auto generated from 1`() = runBlocking<Unit> {
        assertThat(games.insertOne(DbGame()).getOrThrow()).isEqualTo(1)
        assertThat(games.insertOne(DbGame()).getOrThrow()).isEqualTo(2)
        assertThat(rounds.insertOne(DbRound(gameId = 1, roundId = 0)).getOrThrow()).isTrue()
        assertThat(rounds.getGameRounds(1).getOrThrow().single().roundId).isEqualTo(1)
    }

    @Test
    fun `games are listed by start date desc then id desc`() = runBlocking<Unit> {
        games.insertOne(DbGame(startDate = Date(1000))).getOrThrow()
        games.insertOne(DbGame(startDate = Date(3000))).getOrThrow()
        games.insertOne(DbGame(startDate = Date(1000))).getOrThrow()
        assertThat(games.getAllFlow().first().map { it.gameId }).containsExactly(2L, 3L, 1L).inOrder()
    }

    @Test
    fun `game fields round trip, including null end date and default name`() = runBlocking<Unit> {
        val game = DbGame(nameP1 = "A", nameP2 = "B", nameP3 = "C", nameP4 = "D", startDate = Date(123), endDate = null)
        val id = games.insertOne(game).getOrThrow()
        val stored = games.getOne(id).getOrThrow()
        assertThat(stored).isEqualTo(game.copy(gameId = id))
        assertThat(stored.gameName).isEmpty()
        assertThat(games.updateOne(stored.copy(endDate = Date(456), gameName = "x")).getOrThrow()).isTrue()
        assertThat(games.getOneFlow(id).first().endDate).isEqualTo(Date(456))
        assertThat(games.updateOne(DbGame(gameId = 99)).getOrThrow()).isFalse()
    }

    @Test
    fun `winds are stored as their codes and null means ongoing`() = runBlocking<Unit> {
        val gameId = games.insertOne(DbGame()).getOrThrow()
        rounds.insertOne(DbRound(gameId, 0, TableWinds.NORTH, TableWinds.NONE, 12, 1, 2, 3, 4)).getOrThrow()
        rounds.insertOne(DbRound(gameId, 0)).getOrThrow()
        val cursor = db.openHelper.readableDatabase.query("SELECT winnerInitialSeat, discarderInitialSeat FROM Rounds ORDER BY roundId")
        val raw = mutableListOf<Pair<Int?, Int?>>()
        while (cursor.moveToNext()) {
            raw += (if (cursor.isNull(0)) null else cursor.getInt(0)) to (if (cursor.isNull(1)) null else cursor.getInt(1))
        }
        cursor.close()
        assertThat(raw).containsExactly(3 to -1, null to null).inOrder()
        val stored = rounds.getGameRounds(gameId).getOrThrow()
        assertThat(stored[0].winnerInitialSeat).isEqualTo(TableWinds.NORTH)
        assertThat(stored[0].discarderInitialSeat).isEqualTo(TableWinds.NONE)
        assertThat(listOf(stored[0].penaltyP1, stored[0].penaltyP2, stored[0].penaltyP3, stored[0].penaltyP4)).containsExactly(1, 2, 3, 4).inOrder()
        assertThat(stored[1].isOngoing()).isTrue()
    }

    @Test
    fun `rounds require an existing game (foreign key)`() {
        val result = runBlocking { rounds.insertOne(DbRound(gameId = 42, roundId = 0)) }
        assertThat(result.exceptionOrNull()).isInstanceOf(SQLiteConstraintException::class.java)
    }

    @Test
    fun `a game with rounds cannot be deleted before its rounds`() = runBlocking<Unit> {
        val gameId = games.insertOne(DbGame()).getOrThrow()
        rounds.insertOne(DbRound(gameId, 0)).getOrThrow()
        assertThat(games.deleteOne(gameId).isFailure).isTrue()
        DeleteGameUseCase(games, rounds)(gameId).getOrThrow()
        assertThat(games.getAllFlow().first()).isEmpty()
        assertThat(rounds.getAllFlow().first()).isEmpty()
    }

    @Test
    fun `round updates and deletes report whether a row changed`() = runBlocking<Unit> {
        val gameId = games.insertOne(DbGame()).getOrThrow()
        rounds.insertOne(DbRound(gameId, 0)).getOrThrow()
        val round = rounds.getGameRounds(gameId).getOrThrow().single()
        assertThat(rounds.updateOne(DbRound(gameId, round.roundId, TableWinds.EAST, TableWinds.SOUTH, 8)).getOrThrow()).isTrue()
        assertThat(rounds.getOne(round.roundId).getOrThrow().handPoints).isEqualTo(8)
        assertThat(rounds.updateOne(DbRound(gameId, 999, null, null)).getOrThrow()).isFalse()
        assertThat(rounds.deleteOne(999).getOrThrow()).isFalse()
        assertThat(rounds.deleteOne(round.roundId).getOrThrow()).isTrue()
        assertThat(rounds.deleteGameRounds(gameId).getOrThrow()).isTrue()
        assertThrows(Exception::class.java) { runBlocking { rounds.getOne(round.roundId).getOrThrow() } }
    }

    /** Same golden reports as the pure JVM tests, but going through Room: proves both paths agree. */
    @Test
    fun `importing every golden scenario into Room reproduces its report`() = runBlocking<Unit> {
        val importer = ImportGamesFromJsonUseCase(games, rounds, DeleteGameUseCase(games, rounds))
        val getOne = GetOneGameFlowUseCase(games, rounds)
        GameScenarios.all.forEach { scenario ->
            val dir = "scenarios/${scenario.name}"
            val id = importer.createGamesInDbFrom(Goldens.read("$dir/game.json")).single()
            assertThat(GameReport.render(getOne(id).first())).isEqualTo(Goldens.read("$dir/report.txt"))
        }
        assertThat(GetAllGamesFlowUseCase(games, rounds)().first()).hasSize(GameScenarios.all.size)
    }
}
