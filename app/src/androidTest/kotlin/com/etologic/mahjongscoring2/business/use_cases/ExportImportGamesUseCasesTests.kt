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

package com.etologic.mahjongscoring2.business.use_cases

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.business.use_cases.mappers.toUiGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.games.DefaultGamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.DefaultRoundsRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
@SmallTest
class ExportImportGamesUseCasesTests {

    private lateinit var database: AppDatabase

    private lateinit var gamesDao: GamesDao
    private lateinit var roundsDao: RoundsDao

    private lateinit var gamesRepository: DefaultGamesRepository
    private lateinit var roundsRepository: DefaultRoundsRepository

    private lateinit var exportGameToTextUseCase: ExportGameToTextUseCase
    private lateinit var exportGameToCsvUseCase: ExportGameToCsvUseCase
    private lateinit var exportGameToJsonUseCase: ExportGameToJsonUseCase
    private lateinit var exportGamesToJsonUseCase: ExportGamesToJsonUseCase
    private lateinit var importGamesFromJsonUseCase: ImportGamesFromJsonUseCase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        gamesDao = database.gamesDao
        roundsDao = database.roundsDao

        gamesRepository = DefaultGamesRepository(gamesDao)
        roundsRepository = DefaultRoundsRepository(roundsDao)

        val getOneGameFlowUseCase = GetOneGameFlowUseCase(gamesRepository, roundsRepository)
        val getAllGamesFlowUseCase = GetAllGamesFlowUseCase(gamesRepository, roundsRepository)
        val deleteGameUseCase = DeleteGameUseCase(gamesRepository, roundsRepository)

        exportGameToTextUseCase = ExportGameToTextUseCase(getOneGameFlowUseCase)
        exportGameToCsvUseCase = ExportGameToCsvUseCase(getOneGameFlowUseCase)
        exportGameToJsonUseCase = ExportGameToJsonUseCase(getOneGameFlowUseCase)
        exportGamesToJsonUseCase = ExportGamesToJsonUseCase(getAllGamesFlowUseCase)
        importGamesFromJsonUseCase = ImportGamesFromJsonUseCase(gamesRepository, roundsRepository, deleteGameUseCase)
    }

    @After
    fun finish() {
        database.close()
    }

    @Test
    fun exportGameResultsToTextUseCase() = runBlocking {
        // Given an existing complete game
        val game = createCompleteGame()

        // When we call the UseCase for exporting the final results to text
        val exportedText = exportGameToTextUseCase.invoke(game.gameId).getOrThrow()

        // Then we expect the right text
        val expectedText = "Test Game Name 1\n\n" +
                "1st:    Test Player 4    4    (+145)\n" +
                "2nd:    Test Player 3    2    (-3)\n" +
                "3rd:    Test Player 2    1    (-29)\n" +
                "4th:    Test Player 1    0    (-143)\n\n" +
                "Best hand:    28  -  Test Player 2\n"
        assertThat(exportedText).isEqualTo(expectedText)
    }

    @Test
    fun exportGameToCsvUseCase() = runBlocking {
        // Given an existing complete game
        val uiGame = createCompleteGame()

        // When we call the UseCase for exporting the complete game to csv
        val exportedFileText = exportGameToCsvUseCase.buildCsvText(uiGame)

        // Then we expect the right file name and content
        val expectedFileText =
            "Round,Winner,Discarder,Hand Points,Points Test Player 1,Points Test Player 2,Points Test Player 3,Points Test Player 4,Penalty Test Player 1,Penalty Test Player 2,Penalty Test Player 3,Penalty Test Player 4,Game name,Game start date,Game end date\n" +
                    "1,Test Player 1,Test Player 4,18,42,-8,-8,-26,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,42,-8,-8,-26,-,-,-,-,-,-,-\n" +
                    "2,Test Player 2,Test Player 3,9,-8,33,-17,-8,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,34,25,-25,-34,-,-,-,-,-,-,-\n" +
                    "3,Test Player 3,Test Player 2,12,-8,-20,36,-8,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,26,5,11,-42,-,-,-,-,-,-,-\n" +
                    "4,Test Player 4,Test Player 1,25,-33,-8,-8,49,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-7,-3,3,7,-,-,-,-,-,-,-\n" +
                    "5,Test Player 4,-,8,-16,-16,-16,48,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-23,-19,-13,55,-,-,-,-,-,-,-\n" +
                    "6,Test Player 2,Test Player 4,12,-38,46,2,-10,-30,10,10,10,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-61,27,-11,45,-,-,-,-,-,-,-\n" +
                    "7,Test Player 1,Test Player 3,10,34,-8,-18,-8,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-27,19,-29,37,-,-,-,-,-,-,-\n" +
                    "8,Test Player 3,Test Player 2,12,-8,-20,36,-8,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-35,-1,7,29,-,-,-,-,-,-,-\n" +
                    "9,Test Player 4,Test Player 1,17,-25,-8,-8,41,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-60,-9,-1,70,-,-,-,-,-,-,-\n" +
                    "10,Test Player 4,-,8,-16,-16,-16,48,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-76,-25,-17,118,-,-,-,-,-,-,-\n" +
                    "11,Test Player 2,Test Player 4,28,-68,72,-18,-16,-60,20,-10,20,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-144,47,-35,102,-,-,-,-,-,-,-\n" +
                    "12,Test Player 1,Test Player 3,9,33,-8,-17,-8,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-111,39,-52,94,-,-,-,-,-,-,-\n" +
                    "13,Test Player 3,Test Player 2,12,-8,-20,36,-8,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-119,19,-16,86,-,-,-,-,-,-,-\n" +
                    "14,Test Player 3,Test Player 4,13,-8,-8,37,-21,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-127,11,21,65,-,-,-,-,-,-,-\n" +
                    "15,Test Player 4,Test Player 3,8,-8,-8,-16,32,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-135,3,5,97,-,-,-,-,-,-,-\n" +
                    "16,Test Player 4,Test Player 2,24,-8,-32,-8,48,0,0,0,0,Test Game Name 1,${uiGame.startDate},${uiGame.endDate}\n" +
                    "-,-,-,-,-143,-29,-3,145,-,-,-,-,-,-,-\n"
        assertThat(exportedFileText).isEqualTo(expectedFileText)
    }

    @Test
    fun exportAndImportAGameTest() = runBlocking {
        // Given an existing complete game
        val uiGameToExport = createCompleteGame()

        // Then we call the UseCase to get the exported game json
        val exportedGameJson = exportGameToJsonUseCase.jsonFrom(uiGameToExport)

        // Then we call the UseCase to import the exported game
        importGamesFromJsonUseCase.createGamesInDbFrom(exportedGameJson)

        // Then we expect to have the new values in the database
        val dbGames = gamesDao.getAllFlow().first()
        assertThat(dbGames).hasSize(2)

        val importedDbGame = dbGames.first()
        assertThat(importedDbGame.gameId).isEqualTo(2)
        assertThat(importedDbGame.gameName).isEqualTo("Test Game Name 1")
        assertThat(importedDbGame.nameP1).isEqualTo("Test Player 1")
        assertThat(importedDbGame.nameP2).isEqualTo("Test Player 2")
        assertThat(importedDbGame.nameP3).isEqualTo("Test Player 3")
        assertThat(importedDbGame.nameP4).isEqualTo("Test Player 4")
        assertThat(importedDbGame.startDate).isNotNull()
        assertThat(importedDbGame.endDate).isNotNull()

        val importedDbRounds = roundsDao.getGameRounds(importedDbGame.gameId)
        val importedUiGame = importedDbGame.toUiGame(importedDbRounds)
        assertThat(importedUiGame.uiRounds).hasSize(16)
        assertThat(importedUiGame.uiRounds[10].isBestHand).isTrue()

        val lastImportedUiRound = importedUiGame.uiRounds.last()
        assertThat(lastImportedUiRound.roundId).isEqualTo(18 + uiGameToExport.uiRounds.size)
        assertThat(lastImportedUiRound.roundNumber).isEqualTo(16)
        assertThat(lastImportedUiRound.totalPointsP1).isEqualTo(-143)
        assertThat(lastImportedUiRound.totalPointsP2).isEqualTo(-29)
        assertThat(lastImportedUiRound.totalPointsP3).isEqualTo(-3)
        assertThat(lastImportedUiRound.totalPointsP4).isEqualTo(145)
    }

    @Test
    fun exportAndImportSeveralGamesTest() = runBlocking {
        // Given an existing complete game
        val uiGameToExport1 = createCompleteGame(
            gameName = "Test Game Name 1",
            nameP1 = "Test Player 11",
            nameP2 = "Test Player 12",
            nameP3 = "Test Player 13",
            nameP4 = "Test Player 14",
        )
        delay(1.seconds)
        val uiGameToExport2 = createCompleteGame(
            gameName = "Test Game Name 2",
            nameP1 = "Test Player 21",
            nameP2 = "Test Player 22",
            nameP3 = "Test Player 23",
            nameP4 = "Test Player 24",
        )
        delay(1.seconds)
        var uiGameToExport3 = createCompleteGame(
            gameName = "Test Game Name 3",
            nameP1 = "Test Player 31",
            nameP2 = "Test Player 32",
            nameP3 = "Test Player 33",
            nameP4 = "Test Player 34",
            endDate = null,
        )
        // Make 3rd game last round ongoing
        roundsDao.deleteOne(uiGameToExport3.uiRounds.last().roundId)
        roundsDao.insertOne(DbRound(uiGameToExport3.gameId, NOT_SET_GAME_ID))
        val dbGameToExport3 = gamesDao.getAllFlow().first().first()
        val dbRoundsGameToExport3 = roundsDao.getGameRounds(uiGameToExport3.gameId)
        uiGameToExport3 = dbGameToExport3.toUiGame(dbRoundsGameToExport3)

        // Then we call the UseCase to get the exported game json
        val exportedGamesJson = exportGamesToJsonUseCase.jsonFrom(
            listOf(uiGameToExport1, uiGameToExport2, uiGameToExport3)
        )

        // Then we call the UseCase to import the exported game
        importGamesFromJsonUseCase.createGamesInDbFrom(exportedGamesJson)

        // Then we expect to have the new values in the database
        gamesDao.getAllFlow().first().let { dbGames ->
            assertThat(dbGames).hasSize(6)

            dbGames[4].let { importedDbGame ->
                assertThat(importedDbGame.gameId).isEqualTo(4)
                assertThat(importedDbGame.gameName).isEqualTo("Test Game Name 1")
                assertThat(importedDbGame.gameName).isEqualTo(dbGames[5].gameName)
                assertThat(importedDbGame.nameP1).isEqualTo("Test Player 11")
                assertThat(importedDbGame.nameP2).isEqualTo("Test Player 12")
                assertThat(importedDbGame.nameP3).isEqualTo("Test Player 13")
                assertThat(importedDbGame.nameP4).isEqualTo("Test Player 14")
                assertThat(importedDbGame.startDate).isNotNull()
                assertThat(importedDbGame.startDate).isEqualTo(dbGames[5].startDate)
                assertThat(importedDbGame.endDate).isNotNull()

                val importedDbRounds = roundsDao.getGameRounds(importedDbGame.gameId)
                val importedUiGame = importedDbGame.toUiGame(importedDbRounds)
                assertThat(importedUiGame.uiRounds).hasSize(16)
                assertThat(importedUiGame.uiRounds[10].isBestHand).isTrue()

                val lastImportedUiRound = importedUiGame.uiRounds.last()
                assertThat(lastImportedUiRound.roundId).isEqualTo(18 * 3 + 16 * 1 + 1)
                assertThat(lastImportedUiRound.roundNumber).isEqualTo(16)
                assertThat(lastImportedUiRound.totalPointsP1).isEqualTo(-143)
                assertThat(lastImportedUiRound.totalPointsP2).isEqualTo(-29)
                assertThat(lastImportedUiRound.totalPointsP3).isEqualTo(-3)
                assertThat(lastImportedUiRound.totalPointsP4).isEqualTo(145)
            }

            dbGames[2].let { importedDbGame ->
                assertThat(importedDbGame.gameId).isEqualTo(5)
                assertThat(importedDbGame.gameName).isEqualTo("Test Game Name 2")
                assertThat(importedDbGame.gameName).isEqualTo(dbGames[3].gameName)
                assertThat(importedDbGame.nameP1).isEqualTo("Test Player 21")
                assertThat(importedDbGame.nameP2).isEqualTo("Test Player 22")
                assertThat(importedDbGame.nameP3).isEqualTo("Test Player 23")
                assertThat(importedDbGame.nameP4).isEqualTo("Test Player 24")
                assertThat(importedDbGame.startDate).isNotNull()
                assertThat(importedDbGame.startDate).isEqualTo(dbGames[3].startDate)
                assertThat(importedDbGame.endDate).isNotNull()

                val importedDbRounds = roundsDao.getGameRounds(importedDbGame.gameId)
                val importedUiGame = importedDbGame.toUiGame(importedDbRounds)
                assertThat(importedUiGame.uiRounds).hasSize(16)
                assertThat(importedUiGame.uiRounds[10].isBestHand).isTrue()

                val lastImportedUiRound = importedUiGame.uiRounds.last()
                assertThat(lastImportedUiRound.roundId).isEqualTo(18 * 3 + 16 * 2 + 1)
                assertThat(lastImportedUiRound.roundNumber).isEqualTo(16)
                assertThat(lastImportedUiRound.totalPointsP1).isEqualTo(-143)
                assertThat(lastImportedUiRound.totalPointsP2).isEqualTo(-29)
                assertThat(lastImportedUiRound.totalPointsP3).isEqualTo(-3)
                assertThat(lastImportedUiRound.totalPointsP4).isEqualTo(145)
            }

            dbGames[0].let { importedDbGame ->
                assertThat(importedDbGame.gameId).isEqualTo(6)
                assertThat(importedDbGame.gameName).isEqualTo("Test Game Name 3")
                assertThat(importedDbGame.gameName).isEqualTo(dbGames[1].gameName)
                assertThat(importedDbGame.nameP1).isEqualTo("Test Player 31")
                assertThat(importedDbGame.nameP2).isEqualTo("Test Player 32")
                assertThat(importedDbGame.nameP3).isEqualTo("Test Player 33")
                assertThat(importedDbGame.nameP4).isEqualTo("Test Player 34")
                assertThat(importedDbGame.startDate).isNotNull()
                assertThat(importedDbGame.startDate).isEqualTo(dbGames[1].startDate)
                assertThat(importedDbGame.endDate).isNull()

                val importedDbRounds = roundsDao.getGameRounds(importedDbGame.gameId)
                val importedUiGame = importedDbGame.toUiGame(importedDbRounds)
                assertThat(importedUiGame.uiRounds).hasSize(16)
                assertThat(importedUiGame.uiRounds[10].isBestHand).isTrue()

                val lastImportedUiRound = importedUiGame.uiRounds.last()
                assertThat(lastImportedUiRound.roundId).isEqualTo(18 * 3 + 16 * 3 + 1)
                assertThat(lastImportedUiRound.roundNumber).isEqualTo(16)
                assertThat(lastImportedUiRound.winnerInitialSeat).isNull()
                assertThat(lastImportedUiRound.discarderInitialSeat).isNull()
                assertThat(lastImportedUiRound.handPoints).isEqualTo(0)
                assertThat(lastImportedUiRound.pointsP1).isEqualTo(0)
                assertThat(lastImportedUiRound.pointsP2).isEqualTo(0)
                assertThat(lastImportedUiRound.pointsP3).isEqualTo(0)
                assertThat(lastImportedUiRound.pointsP4).isEqualTo(0)
                assertThat(lastImportedUiRound.totalPointsP1).isEqualTo(-135)
                assertThat(lastImportedUiRound.totalPointsP2).isEqualTo(3)
                assertThat(lastImportedUiRound.totalPointsP3).isEqualTo(5)
                assertThat(lastImportedUiRound.totalPointsP4).isEqualTo(97)
            }
        }
    }

    private suspend fun createCompleteGame(
        gameName: String = "Test Game Name 1",
        nameP1: String = "Test Player 1",
        nameP2: String = "Test Player 2",
        nameP3: String = "Test Player 3",
        nameP4: String = "Test Player 4",
        startDate: Date = Date(),
        endDate: Date? = Date(Date().time + 1000)
    ): UiGame {
        var dbGame = DbGame(
            gameId = NOT_SET_GAME_ID,
            gameName = gameName,
            nameP1 = nameP1,
            nameP2 = nameP2,
            nameP3 = nameP3,
            nameP4 = nameP4,
            startDate = startDate,
            endDate = endDate,
        )
        val gameId = gamesRepository.insertOne(dbGame).getOrThrow()
        dbGame = dbGame.copy(gameId = gameId)

        /*  1  (1) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, EAST, NORTH, 18))
        /*  2  (2) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, SOUTH, WEST, 9))
        /*  3  (3) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, WEST, SOUTH, 12))
        /*  4  (4) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, NORTH, EAST, 25))
        /*  5  (5) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, NORTH, NONE, 8))
        /*  6  (6) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, SOUTH, NORTH, 12, -30, 10, 10, 10))
        /*  7  (7) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, EAST, WEST, 10))
        /*  8  (8) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, WEST, SOUTH, 12))
        /*         */
        val round9Id = roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID))
        /*         */ roundsDao.deleteOne(round9Id)
        /*  9 (10) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, NORTH, EAST, 17))
        /* 10 (11) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, NORTH, NONE, 8))
        /* 11 (12) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, SOUTH, NORTH, 28, -60, 20, -10, 20))
        /* 12 (13) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, EAST, WEST, 9))
        /* 13 (14) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, WEST, SOUTH, 12))
        /* 14 (15) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, WEST, NORTH, 13))
        /* 15 (16) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, NORTH, WEST, 8))
        /*         */
        val round17Id = roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID))
        /*         */ roundsDao.deleteOne(round17Id)
        /* 16 (18) */ roundsDao.insertOne(DbRound(gameId, NOT_SET_GAME_ID, NORTH, SOUTH, 24))

        val dbRounds = roundsDao.getGameRounds(gameId)
        return dbGame.toUiGame(dbRounds)
    }
}
