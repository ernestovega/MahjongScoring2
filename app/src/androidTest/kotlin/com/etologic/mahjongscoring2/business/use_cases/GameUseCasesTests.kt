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
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.use_cases.mappers.toUiGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.repositories.games.DefaultGamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.DefaultRoundsRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class GameUseCasesTests {

    private lateinit var database: AppDatabase

    private lateinit var gamesDao: GamesDao
    private lateinit var roundsDao: RoundsDao

    private lateinit var gamesRepository: DefaultGamesRepository
    private lateinit var roundsRepository: DefaultRoundsRepository

    private lateinit var createGameUseCase: CreateGameUseCase
    private lateinit var endGameUseCase: EndGameUseCase
    private lateinit var resumeGameUseCase: ResumeGameUseCase
    private lateinit var endRoundUseCase: EndRoundUseCase
    private lateinit var huDrawUseCase: HuDrawUseCase
    private lateinit var huSelfPickUseCase: HuSelfPickUseCase
    private lateinit var huDiscardUseCase: HuDiscardUseCase
    private lateinit var deleteRoundUseCase: DeleteRoundUseCase
    private lateinit var setPenaltyUseCase: SetPenaltyUseCase
    private lateinit var cancelAllPenaltiesUseCase: CancelAllPenaltiesUseCase
    private lateinit var editGameNamesUseCase: EditGameNamesUseCase

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

        createGameUseCase = CreateGameUseCase(gamesRepository, roundsRepository)
        endGameUseCase = EndGameUseCase(gamesRepository)
        resumeGameUseCase = ResumeGameUseCase(gamesRepository, roundsRepository)
        endRoundUseCase = EndRoundUseCase(roundsRepository, endGameUseCase, getOneGameFlowUseCase)
        huDrawUseCase = HuDrawUseCase(roundsRepository, endRoundUseCase)
        huSelfPickUseCase = HuSelfPickUseCase(roundsRepository, endRoundUseCase)
        huDiscardUseCase = HuDiscardUseCase(roundsRepository, endRoundUseCase)
        deleteRoundUseCase = DeleteRoundUseCase(roundsRepository)
        setPenaltyUseCase = SetPenaltyUseCase(roundsRepository)
        cancelAllPenaltiesUseCase = CancelAllPenaltiesUseCase(roundsRepository)
        editGameNamesUseCase = EditGameNamesUseCase(gamesRepository)
    }

    @After
    fun finish() {
        database.close()
    }

    @Test
    fun createGameUseCase() = runBlocking {
        // Given an empty db
        // When we call the UseCase for creating a game
        createJustGameAndFirstOngoingRound()

        // Then we expect the right game values and the first ongoing round
        val games = gamesDao.getAllFlow().first()
        assertThat(games).hasSize(1)

        val game = games.first()
        assertThat(game.gameId).isEqualTo(1)
        assertThat(game.gameName).isEqualTo("Test Game Name 1")
        assertThat(game.nameP1).isEqualTo("Test Player 1")
        assertThat(game.nameP2).isEqualTo("Test Player 2")
        assertThat(game.nameP3).isEqualTo("Test Player 3")
        assertThat(game.nameP4).isEqualTo("Test Player 4")
        assertThat(game.startDate).isNotNull()
        assertThat(game.endDate).isNull()

        val rounds = roundsDao.getGameRounds(1)
        assertThat(rounds).hasSize(1)

        val firstRound = rounds.first()
        assertThat(firstRound.gameId).isEqualTo(1)
        assertThat(firstRound.roundId).isEqualTo(1)
        assertThat(firstRound.winnerInitialSeat).isNull()
        assertThat(firstRound.discarderInitialSeat).isNull()
        assertThat(firstRound.handPoints).isEqualTo(0)
        assertThat(firstRound.penaltyP1).isEqualTo(0)
        assertThat(firstRound.penaltyP2).isEqualTo(0)
        assertThat(firstRound.penaltyP3).isEqualTo(0)
        assertThat(firstRound.penaltyP4).isEqualTo(0)
    }

    @Test
    fun endGameUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for ending the game
        endGameUseCase.invoke(game.toUiGame(rounds))

        // Then we expect a not null game endDate, and an ongoing round
        game = gamesDao.getAllFlow().first().first()
        assertThat(game.endDate).isNotNull()

        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isNull()
        assertThat(firstRound.discarderInitialSeat).isNull()
    }

    @Test
    fun resumeGameUseCase() = runBlocking {
        // Given an ended game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)
        endGameUseCase.invoke(game.toUiGame(rounds))

        // When we call the UseCase for resuming the game
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)
        resumeGameUseCase.invoke(game.toUiGame(rounds))

        // Then we expect a null game endDate, and an ongoing round
        game = gamesDao.getAllFlow().first().first()
        assertThat(game.endDate).isNull()

        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isNull()
        assertThat(firstRound.discarderInitialSeat).isNull()
    }

    @Test
    fun huDrawUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a Draw
        huDrawUseCase.invoke(game.toUiGame(rounds))

        // Then we expect 2 rounds, the 1st with Draw data and the 2nd ongoing
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isEqualTo(NONE)
        assertThat(firstRound.discarderInitialSeat).isEqualTo(NONE)
        assertThat(firstRound.handPoints).isEqualTo(0)
        val secondRound = rounds.last()
        assertThat(secondRound.winnerInitialSeat).isNull()
        assertThat(secondRound.discarderInitialSeat).isNull()
    }

    @Test
    fun huSelfPickUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a Hu by self pick
        huSelfPickUseCase.invoke(game.toUiGame(rounds), HuData(8, EAST))

        // Then we expect 2 rounds, the 1st with the Hu by self pick data and the 2nd ongoing
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)

        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isEqualTo(EAST)
        assertThat(firstRound.discarderInitialSeat).isEqualTo(NONE)
        assertThat(firstRound.handPoints).isEqualTo(8)

        val secondRound = rounds.last()
        assertThat(secondRound.winnerInitialSeat).isNull()
        assertThat(secondRound.discarderInitialSeat).isNull()
    }

    @Test
    fun huDiscardUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a Hu by discard
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(9, EAST, SOUTH))

        // Then we expect 2 rounds, the 1st with Hu by discard data and the 2nd ongoing
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)

        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isEqualTo(EAST)
        assertThat(firstRound.discarderInitialSeat).isEqualTo(SOUTH)
        assertThat(firstRound.handPoints).isEqualTo(9)

        val secondRound = rounds.last()
        assertThat(secondRound.winnerInitialSeat).isNull()
        assertThat(secondRound.discarderInitialSeat).isNull()
    }

    @Test
    fun end16thRoundUseCase() = runBlocking {
        // Given an existing game with the 16th round ongoing
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)
        repeat(MAX_MCR_ROUNDS - 1) {
            huDrawUseCase.invoke(game.toUiGame(rounds))
            rounds = roundsDao.getGameRounds(game.gameId)
        }

        // When set the 16th hand by Hu self pick
        huSelfPickUseCase.invoke(game.toUiGame(rounds), HuData(8, EAST))

        // Then we expect MAX_MCR_ROUNDS rounds, none ongoing and a not null game endDate
        rounds = roundsDao.getGameRounds(game.gameId)
        assertThat(rounds).hasSize(MAX_MCR_ROUNDS)

        val lastRound = rounds.last()
        assertThat(lastRound.winnerInitialSeat).isNotNull()
        assertThat(lastRound.discarderInitialSeat).isNotNull()

        game = gamesDao.getAllFlow().first().first()
        assertThat(game.endDate).isNotNull()
    }

    @Test
    fun deleteRoundUseCase() = runBlocking {
        // Given an existing game with 2 rounds, last 1 ongoing
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)
        huDrawUseCase.invoke(game.toUiGame(rounds))

        // When we call the UseCase to delete the round 1
        var firstRound = rounds.first()
        deleteRoundUseCase.invoke(firstRound.roundId)

        // Then we expect 1 round ongoing
        rounds = roundsDao.getGameRounds(game.gameId)
        assertThat(rounds).hasSize(1)

        firstRound = rounds.first()
        assertThat(firstRound.roundId).isEqualTo(2)
        assertThat(firstRound.winnerInitialSeat).isNull()
        assertThat(firstRound.discarderInitialSeat).isNull()
    }

    @Test
    fun delete16thRoundUseCase() = runBlocking {
        // Given an ended game with MAX_MCR_ROUNDS rounds
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)
        repeat(MAX_MCR_ROUNDS) {
            huDrawUseCase.invoke(game.toUiGame(rounds))
            rounds = roundsDao.getGameRounds(game.gameId)
        }

        // When we call the UseCase to delete the last round
        var lastRound = rounds.last()
        deleteRoundUseCase.invoke(lastRound.roundId)

        // Then we expect 15 rounds, none ongoing and game endDate not null
        rounds = roundsDao.getGameRounds(game.gameId)
        assertThat(rounds).hasSize(15)

        lastRound = rounds.last()
        assertThat(lastRound.roundId).isEqualTo(15)
        assertThat(lastRound.winnerInitialSeat).isNotNull()
        assertThat(lastRound.discarderInitialSeat).isNotNull()

        game = gamesDao.getAllFlow().first().first()
        assertThat(game.endDate).isNotNull()
    }

    @Test
    fun setDividedPenaltyUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a penalty divided
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.first(), PenaltyData(30, true, EAST))

        // Then we expect 1 ongoing round with the right penalty data
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isNull()
        assertThat(firstRound.discarderInitialSeat).isNull()
        assertThat(firstRound.penaltyP1).isEqualTo(-30)
        assertThat(firstRound.penaltyP2).isEqualTo(+10)
        assertThat(firstRound.penaltyP3).isEqualTo(+10)
        assertThat(firstRound.penaltyP4).isEqualTo(+10)
    }

    @Test
    fun setNotDividedPenaltyUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a penalty divided
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.first(), PenaltyData(30, false, EAST))

        // Then we expect 1 ongoing round with the right penalty data
        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isNull()
        assertThat(firstRound.discarderInitialSeat).isNull()
        assertThat(firstRound.penaltyP1).isEqualTo(-30)
        assertThat(firstRound.penaltyP2).isEqualTo(0)
        assertThat(firstRound.penaltyP3).isEqualTo(0)
        assertThat(firstRound.penaltyP4).isEqualTo(0)
    }

    @Test
    fun cancelPenaltiesUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a penalty divided
        cancelAllPenaltiesUseCase.invoke(game.toUiGame(rounds).uiRounds.first())

        // Then we expect 1 ongoing round with no penalties
        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isNull()
        assertThat(firstRound.discarderInitialSeat).isNull()
        assertThat(firstRound.penaltyP1).isEqualTo(0)
        assertThat(firstRound.penaltyP2).isEqualTo(0)
        assertThat(firstRound.penaltyP3).isEqualTo(0)
        assertThat(firstRound.penaltyP4).isEqualTo(0)
    }

    @Test
    fun editGameNamesUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        var game = createJustGameAndFirstOngoingRound()
        val rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for setting a penalty divided
        editGameNamesUseCase.invoke(
            uiGame = game.toUiGame(rounds),
            newGameName = "EDITED game name 1",
            newNameP1 = "EDITED Player 1",
            newNameP2 = "EDITED Player 2",
            newNameP3 = "EDITED Player 3",
            newNameP4 = "EDITED Player 4",
        )

        // Then we expect 1 ongoing round with no penalties
        game = gamesDao.getAllFlow().first().first()
        assertThat(game.gameName).isEqualTo("EDITED game name 1")
        assertThat(game.nameP1).isEqualTo("EDITED Player 1")
        assertThat(game.nameP2).isEqualTo("EDITED Player 2")
        assertThat(game.nameP3).isEqualTo("EDITED Player 3")
        assertThat(game.nameP4).isEqualTo("EDITED Player 4")
    }

    @Test
    fun fullGameTest() = runBlocking {
        // Given a complete game
        val uiGame = createCompleteGame()

        // Then we check the expected data
        assertThat(uiGame.endDate).isNotNull()
        assertThat(uiGame.uiRounds).hasSize(16)
        val lastUiRound = uiGame.uiRounds.last()
        assertThat(lastUiRound.roundId).isEqualTo(18)
        assertThat(lastUiRound.roundNumber).isEqualTo(16)
        assertThat(lastUiRound.totalPointsP1).isEqualTo(-143)
        assertThat(lastUiRound.totalPointsP2).isEqualTo(-29)
        assertThat(lastUiRound.totalPointsP3).isEqualTo(-3)
        assertThat(lastUiRound.totalPointsP4).isEqualTo(145)
        assertThat(uiGame.uiRounds[10].isBestHand).isTrue()
    }

    private suspend fun createCompleteGame(): UiGame {
        val game = createJustGameAndFirstOngoingRound()
        var rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(18, EAST, TableWinds.NORTH)) // 1
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(9, SOUTH, TableWinds.WEST)) // 2
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(12, TableWinds.WEST, SOUTH)) // 3
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(25, TableWinds.NORTH, EAST)) // 4
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds), HuData(8, TableWinds.NORTH)) // 5
        rounds = roundsDao.getGameRounds(game.gameId)
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.last(), PenaltyData(30, true, EAST)) // 6
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(12, SOUTH, TableWinds.NORTH)) // 6
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(10, EAST, TableWinds.WEST)) // 7
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(12, TableWinds.WEST, SOUTH)) // 8
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds), HuData(8, TableWinds.NORTH)) // 9
        deleteRoundUseCase.invoke(9) // 9
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(17, TableWinds.NORTH, EAST)) // 9 (10)
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds), HuData(8, TableWinds.NORTH)) // 10 (11)
        rounds = roundsDao.getGameRounds(game.gameId)
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.last(), PenaltyData(60, true, EAST)) // 11 (12)
        rounds = roundsDao.getGameRounds(game.gameId)
        setPenaltyUseCase.invoke(game.toUiGame(rounds).uiRounds.last(), PenaltyData(30, false, TableWinds.WEST)) // 11 (12)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(28, SOUTH, TableWinds.NORTH)) // 11 (12)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(9, EAST, TableWinds.WEST)) // 12 (13)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(12, TableWinds.WEST, SOUTH)) // 13 (14)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(13, TableWinds.WEST, TableWinds.NORTH)) // 14 (15)
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(8, TableWinds.NORTH, TableWinds.WEST)) // 15 (16)
        rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase.invoke(game.toUiGame(rounds), HuData(1008, TableWinds.NORTH)) // 16 (17)
        deleteRoundUseCase.invoke(17) // 16 (17)
        rounds = roundsDao.getGameRounds(game.gameId)
        resumeGameUseCase.invoke(game.toUiGame(rounds))
        rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase.invoke(game.toUiGame(rounds), HuData(24, TableWinds.NORTH, SOUTH)) // 16 (18)

        val dbGame = createJustGameAndFirstOngoingRound()
        val dbRounds = roundsDao.getGameRounds(game.gameId)
        return dbGame.toUiGame(dbRounds)
    }

    private suspend fun createJustGameAndFirstOngoingRound(gameName: String = "Test Game Name 1"): DbGame {
        createGameUseCase.invoke(
            gameName = gameName,
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )
        return gamesDao.getAllFlow().first().last()
    }
}