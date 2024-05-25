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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.use_cases.mappers.toUiGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.BaseDatabaseTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class GameUseCasesTests : BaseDatabaseTest() {

    private lateinit var createGameUseCase: CreateGameUseCase
    private lateinit var endGameUseCase: EndGameUseCase
    private lateinit var resumeGameUseCase: ResumeGameUseCase
    private lateinit var huDrawUseCase: HuDrawUseCase
    private lateinit var huSelfPickUseCase: HuSelfPickUseCase
    private lateinit var huDiscardUseCase: HuDiscardUseCase
    private lateinit var deleteRoundUseCase: DeleteRoundUseCase

    @Before
    fun setUp() {
        initDataBaseAndRepos()
        createGameUseCase = CreateGameUseCase(gamesRepository, roundsRepository)
        endGameUseCase = EndGameUseCase(gamesRepository)
        resumeGameUseCase = ResumeGameUseCase(gamesRepository, roundsRepository)
        val getOneGameUseCase = GetOneGameUseCase(gamesRepository, roundsRepository)
        val endRoundUseCase = EndRoundUseCase(roundsRepository, endGameUseCase, getOneGameUseCase)
        huDrawUseCase = HuDrawUseCase(roundsRepository, endRoundUseCase)
        huSelfPickUseCase = HuSelfPickUseCase(roundsRepository, endRoundUseCase)
        huDiscardUseCase = HuDiscardUseCase(roundsRepository, endRoundUseCase)
        deleteRoundUseCase = DeleteRoundUseCase(roundsRepository)
    }

    @After
    fun finish() {
        closeDatabase()
    }

    @Test
    fun createGameUseCase() = runBlocking {
        // Given an empty db
        // When we call the UseCase for creating a game
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )

        // Then we expect the right game values and the first ongoing round
        val games = gamesDao.getAllFlow().first()
        assertThat(games).hasSize(1)

        val game = games.first()
        assertThat(game.gameId).isEqualTo(1)
        assertThat(game.gameName).isEqualTo("Test game name 1")
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
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )
        var game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)

        // When we call the UseCase for ending the game
        endGameUseCase(game.toUiGame(rounds))

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
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )
        var game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)
        endGameUseCase(game.toUiGame(rounds))

        // When we call the UseCase for resuming the game
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)
        resumeGameUseCase(game.toUiGame(rounds))

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
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )

        // When we call the UseCase for setting a Draw
        var game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)
        huDrawUseCase(game.toUiGame(rounds))

        // Then we expect 2 rounds, the 1st with Draw data and the 2nd ongoing
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)
        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isEqualTo(TableWinds.NONE)
        assertThat(firstRound.discarderInitialSeat).isEqualTo(TableWinds.NONE)
        assertThat(firstRound.handPoints).isEqualTo(0)
        val secondRound = rounds.last()
        assertThat(secondRound.winnerInitialSeat).isNull()
        assertThat(secondRound.discarderInitialSeat).isNull()
    }

    @Test
    fun huSelfPickUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )

        // When we call the UseCase for setting a Hu by self pick
        var game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)
        huSelfPickUseCase(game.toUiGame(rounds), HuData(8, TableWinds.EAST))

        // Then we expect 2 rounds, the 1st with the Hu by self pick data and the 2nd ongoing
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)

        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isEqualTo(TableWinds.EAST)
        assertThat(firstRound.discarderInitialSeat).isEqualTo(TableWinds.NONE)
        assertThat(firstRound.handPoints).isEqualTo(8)

        val secondRound = rounds.last()
        assertThat(secondRound.winnerInitialSeat).isNull()
        assertThat(secondRound.discarderInitialSeat).isNull()
    }

    @Test
    fun huDiscardUseCase() = runBlocking {
        // Given an existing game with 1 ongoing round
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )

        // When we call the UseCase for setting a Hu by discard
        var game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)
        huDiscardUseCase(game.toUiGame(rounds), HuData(9, TableWinds.EAST, TableWinds.SOUTH))

        // Then we expect 2 rounds, the 1st with Hu by discard data and the 2nd ongoing
        game = gamesDao.getAllFlow().first().first()
        rounds = roundsDao.getGameRounds(game.gameId)

        val firstRound = rounds.first()
        assertThat(firstRound.winnerInitialSeat).isEqualTo(TableWinds.EAST)
        assertThat(firstRound.discarderInitialSeat).isEqualTo(TableWinds.SOUTH)
        assertThat(firstRound.handPoints).isEqualTo(9)

        val secondRound = rounds.last()
        assertThat(secondRound.winnerInitialSeat).isNull()
        assertThat(secondRound.discarderInitialSeat).isNull()
    }

    @Test
    fun end16thRoundUseCase() = runBlocking {
        // Given an existing game with the 16th round ongoing
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )
        var game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)
        repeat(MAX_MCR_ROUNDS - 1) {
            huDrawUseCase(game.toUiGame(rounds))
            rounds = roundsDao.getGameRounds(game.gameId)
        }

        // When set the 16th hand by Hu self pick
        huSelfPickUseCase(game.toUiGame(rounds), HuData(8, TableWinds.EAST))

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
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )
        val game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)
        huDrawUseCase(game.toUiGame(rounds))

        // When we call the UseCase to delete the round 1
        var firstRound = rounds.first()
        deleteRoundUseCase.invoke(game.gameId, firstRound.roundId)

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
        createGameUseCase(
            gameName = "Test game name 1",
            nameP1 = "Test Player 1",
            nameP2 = "Test Player 2",
            nameP3 = "Test Player 3",
            nameP4 = "Test Player 4",
        )
        var game = gamesDao.getAllFlow().first().first()
        var rounds = roundsDao.getGameRounds(game.gameId)
        repeat(MAX_MCR_ROUNDS) {
            huDrawUseCase(game.toUiGame(rounds))
            rounds = roundsDao.getGameRounds(game.gameId)
        }

        // When we call the UseCase to delete the last round
        var lastRound = rounds.last()
        deleteRoundUseCase(game.gameId, lastRound.roundId)

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
}