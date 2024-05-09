/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.etologic.mahjongscoring2.data_source.local_data_sources

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.Migration1to2
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class MigrationsTest {

    private val testDbName = "migration-test"
    private val allMigrations = arrayOf(
        Migration1to2,
    )

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(testDbName, 1)
            .apply { close() }

        // Open latest version of the database. Room validates the schema once all migrations execute.
        Room.databaseBuilder(
            context = InstrumentationRegistry.getInstrumentation().targetContext,
            klass = AppDatabase::class.java,
            name = testDbName
        )
            .addMigrations(*allMigrations)
            .build()
            .apply { openHelper.writableDatabase.close() }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(testDbName, 1)
        // Database has schema version 1. Insert some data using SQL queries.
        // You can't use DAO classes because they expect the latest schema.

        //"INSERT INTO Games VALUES(gameId,NameP1,NameP2,NameP3,NameP4,startDate)"
        db.execSQL("INSERT INTO Games VALUES(1,'Player A','Player B','Player C','Player D',1714862073965)")
        db.execSQL("INSERT INTO Games VALUES(3,'Player A2','Player B2','Player C2','Player D2',1714862254044)")

        //"INSERT INTO Rounds VALUES(winnerInitialSeat,discarderInitialSeat,handPoints,pointsP1,pointsP2,pointsP3,pointsP4,penaltyP1,penaltyP2,penaltyP3,penaltyP4,roundDuration,isEnded,gameId,roundId)"
        db.execSQL("INSERT INTO Rounds VALUES(0,-1,8,48,-16,-16,-16,0,0,0,0,450000,1,1,1)")
        db.execSQL("INSERT INTO Rounds VALUES(1,2,9,-8,33,-17,-8,0,0,0,0,450000,1,1,2)")
        db.execSQL("INSERT INTO Rounds VALUES(2,3,10,-8,-8,34,-18,0,0,0,0,450000,1,1,3)")
        db.execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,1,1,4)")
        db.execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,1,3,21)")
        db.execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,-30,0,0,0,-30,450000,1,3,22)")
        db.execSQL("INSERT INTO Rounds VALUES(-1,-1,0,20,20,20,-60,20,20,20,-60,450000,1,3,23)")
        db.execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,0,3,33)")

        // Prepare for the next version.
        db.close()

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(testDbName, 2, true, Migration1to2)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        val expectedGames = listOf(
            DbGame(
                gameId = 1,
                nameP1 = "Player A",
                nameP2 = "Player B",
                nameP3 = "Player C",
                nameP4 = "Player D",
                startDate = Date(1714862073965),
                endDate = null,
                gameName = "",
            ),
            DbGame(
                gameId = 3,
                nameP1 = "Player A2",
                nameP2 = "Player B2",
                nameP3 = "Player C2",
                nameP4 = "Player D2",
                startDate = Date(1714862254044),
                endDate = null,
                gameName = "",
            ),
        )

        val gamesCursor = db.query("SELECT * FROM Games")
        gamesCursor.moveToFirst()
        do {
            val gameId = gamesCursor.getLong(gamesCursor.getColumnIndex("gameId"))
            val startDate = gamesCursor.getLong(gamesCursor.getColumnIndex("startDate"))
            val endDate = gamesCursor.getLong(gamesCursor.getColumnIndex("endDate"))
            val gameName = gamesCursor.getString(gamesCursor.getColumnIndex("gameName"))
            val expectedGame = expectedGames.find { it.gameId == gameId }
            assertThat(expectedGame).isNotNull()
            assertThat(startDate).isNotNull()
            assertThat(endDate).isEqualTo(startDate)
            assertThat(gameName).isEmpty()
        } while (gamesCursor.moveToNext())
        gamesCursor.close()

        val expectedRounds = listOf(
            DbRound(gameId = 1, roundId = 1).apply {
                winnerInitialSeat = EAST
                discarderInitialSeat = NONE
                handPoints = 8
            },
            DbRound(gameId = 1, roundId = 2).apply {
                winnerInitialSeat = SOUTH
                discarderInitialSeat = WEST
                handPoints = 9
            },
            DbRound(gameId = 1, roundId = 3).apply {
                winnerInitialSeat = WEST
                discarderInitialSeat = NORTH
                handPoints = 10
            },
            DbRound(gameId = 1, roundId = 4).apply {
                winnerInitialSeat = NONE
                discarderInitialSeat = NONE
            },
            DbRound(gameId = 3, roundId = 21).apply {
                winnerInitialSeat = NONE
                discarderInitialSeat = NONE
            },
            DbRound(gameId = 3, roundId = 22).apply {
                winnerInitialSeat = NONE
                discarderInitialSeat = NONE
                penaltyP4 = -30
            },
            DbRound(gameId = 3, roundId = 23).apply {
                winnerInitialSeat = NONE
                discarderInitialSeat = NONE
                penaltyP1 = 20
                penaltyP2 = 20
                penaltyP3 = 20
                penaltyP4 = -60
            },
            DbRound(gameId = 3, roundId = 33).apply {
                winnerInitialSeat = NONE
                discarderInitialSeat = NONE
            },
        )

        val roundsCursor = db.query("SELECT * FROM Rounds")
        roundsCursor.moveToFirst()
        do {
            val gameId = roundsCursor.getLong(roundsCursor.getColumnIndex("gameId"))
            val roundId = roundsCursor.getLong(roundsCursor.getColumnIndex("roundId"))
            val winnerInitialSeat = roundsCursor.getInt(roundsCursor.getColumnIndex("winnerInitialSeat"))
            val discarderInitialSeat = roundsCursor.getInt(roundsCursor.getColumnIndex("discarderInitialSeat"))
            val handPoints = roundsCursor.getInt(roundsCursor.getColumnIndex("handPoints"))
            val penaltyP1 = roundsCursor.getInt(roundsCursor.getColumnIndex("penaltyP1"))
            val penaltyP2 = roundsCursor.getInt(roundsCursor.getColumnIndex("penaltyP2"))
            val penaltyP3 = roundsCursor.getInt(roundsCursor.getColumnIndex("penaltyP3"))
            val penaltyP4 = roundsCursor.getInt(roundsCursor.getColumnIndex("penaltyP4"))
            val expectedRound = expectedRounds.find { it.roundId == roundId }
            assertThat(expectedRound).isNotNull()
            assertThat(gameId).isEqualTo(expectedRound!!.gameId)
            assertThat(winnerInitialSeat).isEqualTo(expectedRound.winnerInitialSeat?.code)
            assertThat(discarderInitialSeat).isEqualTo(expectedRound.discarderInitialSeat?.code)
            assertThat(handPoints).isEqualTo(expectedRound.handPoints)
            assertThat(penaltyP1).isEqualTo(expectedRound.penaltyP1)
            assertThat(penaltyP2).isEqualTo(expectedRound.penaltyP2)
            assertThat(penaltyP3).isEqualTo(expectedRound.penaltyP3)
            assertThat(penaltyP4).isEqualTo(expectedRound.penaltyP4)
        } while (roundsCursor.moveToNext())
        roundsCursor.close()

        db.close()
    }
}
