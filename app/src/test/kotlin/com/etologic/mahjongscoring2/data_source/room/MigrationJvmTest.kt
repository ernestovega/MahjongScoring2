package com.etologic.mahjongscoring2.data_source.room

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.business.use_cases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.Migration1to2
import com.etologic.mahjongscoring2.data_source.repositories.games.DefaultGamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.DefaultRoundsRepository
import com.etologic.mahjongscoring2.testutil.GameReport
import com.etologic.mahjongscoring2.testutil.Goldens
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Database migration test that runs on the JVM (the instrumented MigrationsTest needs a device).
 *
 * The v1 database is created from the exported Room schema `schemas/.../1.json`, filled with data from a
 * real v1 install, migrated by opening it with Room (which validates the result against schema v2) and
 * finally read through the use cases. The resulting games are pinned in `golden/migration_1_to_2.txt`.
 */
@RunWith(AndroidJUnit4::class)
class MigrationJvmTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val dbName = "migration-jvm-test"

    private fun schema(version: Int) = File(
        System.getProperty("room.schemaDir"),
        "${AppDatabase::class.java.name}/$version.json",
    ).readText()

    private fun createV1Database(populate: SupportSQLiteDatabase.() -> Unit) {
        context.deleteDatabase(dbName)
        val database = Json.parseToJsonElement(schema(1)).jsonObject["database"]!!.jsonObject
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    database["entities"]!!.jsonArray.forEach { entity ->
                        val table = entity.jsonObject["tableName"]!!.jsonPrimitive.content
                        db.execSQL(entity.jsonObject["createSql"]!!.jsonPrimitive.content.replace("\${TABLE_NAME}", table))
                        entity.jsonObject["indices"]?.jsonArray?.forEach { index ->
                            db.execSQL(index.jsonObject["createSql"]!!.jsonPrimitive.content.replace("\${TABLE_NAME}", table))
                        }
                    }
                    database["setupQueries"]!!.jsonArray.forEach { db.execSQL(it.jsonPrimitive.content) }
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            })
            .build()
        FrameworkSQLiteOpenHelperFactory().create(config).writableDatabase.apply {
            populate()
            close()
        }
    }

    @Test
    fun `schema files exist for every version`() {
        assertThat(Json.parseToJsonElement(schema(1)).jsonObject["database"]!!.jsonObject["version"]!!.jsonPrimitive.content).isEqualTo("1")
        assertThat(Json.parseToJsonElement(schema(2)).jsonObject["database"]!!.jsonObject["version"]!!.jsonPrimitive.content).isEqualTo("2")
    }

    @Test
    fun `migrate 1 to 2 keeps games and rounds`() = runBlocking<Unit> {
        createV1Database {
            // INSERT INTO Games VALUES(gameId, nameP1, nameP2, nameP3, nameP4, startDate)
            execSQL("INSERT INTO Games VALUES(1,'Player A','Player B','Player C','Player D',1714862073965)")
            execSQL("INSERT INTO Games VALUES(3,'Player A2','Player B2','Player C2','Player D2',1714862254044)")
            // INSERT INTO Rounds VALUES(winner, discarder, handPoints, pointsP1..P4, penaltyP1..P4, roundDuration, isEnded, gameId, roundId)
            execSQL("INSERT INTO Rounds VALUES(0,-1,8,48,-16,-16,-16,0,0,0,0,450000,1,1,1)")
            execSQL("INSERT INTO Rounds VALUES(1,2,9,-8,33,-17,-8,0,0,0,0,450000,1,1,2)")
            execSQL("INSERT INTO Rounds VALUES(2,3,10,-8,-8,34,-18,0,0,0,0,450000,1,1,3)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,1,1,4)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,1,3,21)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,-30,0,0,0,-30,450000,1,3,22)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,20,20,20,-60,20,20,20,-60,450000,1,3,23)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,0,3,33)")
            execSQL("INSERT INTO Combinations VALUES(1,1,'Pure double chow',0,'',0)")
        }

        // Room validates the migrated schema against version 2 when it opens the database.
        val db = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(Migration1to2)
            .allowMainThreadQueries()
            .build()
        try {
            val sqlite = db.openHelper.writableDatabase
            val tables = sqlite.query("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name").use { c ->
                buildList { while (c.moveToNext()) add(c.getString(0)) }
            }
            assertThat(tables).containsNoneOf("Combinations", "Tables", "Rounds_temp")

            val uiGames = GetAllGamesFlowUseCase(DefaultGamesRepository(db.gamesDao), DefaultRoundsRepository(db.roundsDao))().first()
            assertThat(uiGames.map { it.gameId }).containsExactly(3L, 1L).inOrder()
            // Migrated games are considered ended (endDate = startDate) and have no name.
            uiGames.forEach {
                assertThat(it.endDate).isEqualTo(it.startDate)
                assertThat(it.gameName).isEmpty()
            }
            Goldens.assertMatches("migration_1_to_2.txt", uiGames.joinToString("\n") { GameReport.render(it) })
        } finally {
            db.close()
            context.deleteDatabase(dbName)
        }
    }
}
