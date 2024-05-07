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
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.Migration1to2
import com.etologic.mahjongscoring2.data_source.model.DbGame
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class MigrationTest {

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
        db.execSQL("INSERT INTO Games VALUES(1,'Player A','Player B','Player C','Player D',1714862073965)")
        db.execSQL("INSERT INTO Games VALUES(2,'Player A2','Player B2','Player C2','Player D2',1714862254044)")

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
                gameId = 2,
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
            val game = expectedGames.find { it.gameId == gameId }
            assertThat(game).isNotNull()
            assertThat(startDate).isNotNull()
            assertThat(endDate).isEqualTo(startDate)
            assertThat(gameName).isEmpty()
        } while (gamesCursor.moveToNext())
        gamesCursor.close()
        db.close()
    }
}
