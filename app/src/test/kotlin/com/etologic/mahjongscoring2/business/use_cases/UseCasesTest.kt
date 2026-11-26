package com.etologic.mahjongscoring2.business.use_cases

import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.business.model.exceptions.GameRoundsNumberExceededException
import com.etologic.mahjongscoring2.testutil.FakeGamesRepository
import com.etologic.mahjongscoring2.testutil.FakeRoundsRepository
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.etologic.mahjongscoring2.testutil.clone
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Date

/**
 * Behaviour of every business use case against in-memory repositories.
 * (The same flows are exercised against a real Room database in the Robolectric tests.)
 */
class UseCasesTest {

    private lateinit var games: FakeGamesRepository
    private lateinit var rounds: FakeRoundsRepository
    private lateinit var getOneGame: GetOneGameFlowUseCase
    private lateinit var getAllGames: GetAllGamesFlowUseCase
    private lateinit var createGame: CreateGameUseCase
    private lateinit var endGame: EndGameUseCase
    private lateinit var endRound: EndRoundUseCase
    private lateinit var huDiscard: HuDiscardUseCase
    private lateinit var huSelfPick: HuSelfPickUseCase
    private lateinit var huDraw: HuDrawUseCase
    private lateinit var setPenalty: SetPenaltyUseCase
    private lateinit var cancelPenalties: CancelAllPenaltiesUseCase
    private lateinit var deleteRound: DeleteRoundUseCase
    private lateinit var deleteGame: DeleteGameUseCase
    private lateinit var resumeGame: ResumeGameUseCase
    private lateinit var editNames: EditGameNamesUseCase

    @BeforeEach
    fun setUp() {
        games = FakeGamesRepository()
        rounds = FakeRoundsRepository()
        getOneGame = GetOneGameFlowUseCase(games, rounds)
        getAllGames = GetAllGamesFlowUseCase(games, rounds)
        createGame = CreateGameUseCase(games, rounds)
        endGame = EndGameUseCase(games)
        endRound = EndRoundUseCase(rounds, endGame, getOneGame)
        huDiscard = HuDiscardUseCase(rounds, endRound)
        huSelfPick = HuSelfPickUseCase(rounds, endRound)
        huDraw = HuDrawUseCase(rounds, endRound)
        setPenalty = SetPenaltyUseCase(rounds)
        cancelPenalties = CancelAllPenaltiesUseCase(rounds)
        deleteRound = DeleteRoundUseCase(rounds)
        deleteGame = DeleteGameUseCase(games, rounds)
        resumeGame = ResumeGameUseCase(games, rounds)
        editNames = EditGameNamesUseCase(games)
    }

    private suspend fun newGame(): GameId = createGame("Game", "A", "B", "C", "D").getOrThrow()
    private suspend fun load(id: GameId): UiGame = getOneGame(id).first()

    @Nested
    inner class Create {

        @Test
        fun `creates the game with one ongoing round`() = runTest {
            val before = Date()
            val id = createGame("My game", "Ana", "Bruno", "Carla", "David").getOrThrow()
            val game = load(id)
            assertThat(game.gameName).isEqualTo("My game")
            assertThat(game.playersNames.toList()).containsExactly("Ana", "Bruno", "Carla", "David").inOrder()
            assertThat(game.isEnded).isFalse()
            assertThat(game.startDate.time).isAtLeast(before.time)
            assertThat(game.uiRounds).hasSize(1)
            assertThat(game.ongoingRound.isOngoing()).isTrue()
            assertThat(game.ongoingRound.areTherePenalties()).isFalse()
        }

        @Test
        fun `blank game name defaults to the start date`() = runTest {
            val id = createGame("   ", "A", "B", "C", "D").getOrThrow()
            val game = load(id)
            assertThat(game.gameName).matches("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")
        }

        @Test
        fun `player names are stored as typed (not normalized on creation)`() = runTest {
            val id = createGame("g", " \"A\" ", "", "C", "D").getOrThrow()
            assertThat(load(id).playersNames.toList()).containsExactly(" \"A\" ", "", "C", "D").inOrder()
        }

        @Test
        fun `failure is propagated and no round is created`() = runTest {
            games.failure = IllegalStateException("db")
            assertThat(createGame("g", "A", "B", "C", "D").isFailure).isTrue()
            assertThat(rounds.rounds.value).isEmpty()
        }
    }

    @Nested
    inner class Hands {

        @Test
        fun `hu by discard stores the hand and opens the next round`() = runTest {
            val id = newGame()
            assertThat(huDiscard(load(id), HuData(points = 10, winnerInitialSeat = WEST, discarderInitialSeat = NORTH)).getOrThrow()).isTrue()
            val game = load(id)
            assertThat(game.uiRounds).hasSize(2)
            with(game.uiRounds[0]) {
                assertThat(winnerInitialSeat).isEqualTo(WEST)
                assertThat(discarderInitialSeat).isEqualTo(NORTH)
                assertThat(handPoints).isEqualTo(10)
                assertThat(listOf(pointsP1, pointsP2, pointsP3, pointsP4)).containsExactly(-8, -8, 34, -18).inOrder()
            }
            assertThat(game.ongoingRound.isOngoing()).isTrue()
        }

        @Test
        fun `hu by self pick stores NONE as discarder`() = runTest {
            val id = newGame()
            huSelfPick(load(id), HuData(points = 8, winnerInitialSeat = EAST, discarderInitialSeat = SOUTH)).getOrThrow()
            val first = load(id).uiRounds[0]
            assertThat(first.discarderInitialSeat).isEqualTo(NONE)
            assertThat(listOf(first.pointsP1, first.pointsP2, first.pointsP3, first.pointsP4)).containsExactly(48, -16, -16, -16).inOrder()
        }

        @Test
        fun `draw stores NONE winner and NONE discarder`() = runTest {
            val id = newGame()
            huDraw(load(id)).getOrThrow()
            val first = load(id).uiRounds[0]
            assertThat(first.winnerInitialSeat).isEqualTo(NONE)
            assertThat(first.discarderInitialSeat).isEqualTo(NONE)
            assertThat(first.isOngoing()).isFalse()
        }

        @Test
        fun `penalties of the ongoing round are kept when the hand is saved`() = runTest {
            val id = newGame()
            setPenalty(load(id).ongoingRound, PenaltyData(points = 30, isDivided = true, penalizedPlayerInitialSeat = SOUTH)).getOrThrow()
            huDiscard(load(id), HuData(8, EAST, WEST)).getOrThrow()
            val first = load(id).uiRounds[0]
            assertThat(listOf(first.penaltyP1, first.penaltyP2, first.penaltyP3, first.penaltyP4)).containsExactly(10, -30, 10, 10).inOrder()
            assertThat(listOf(first.pointsP1, first.pointsP2, first.pointsP3, first.pointsP4)).containsExactly(42, -38, -6, 2).inOrder()
        }

        @Test
        fun `the 16th hand ends the game instead of opening round 17`() = runTest {
            val id = newGame()
            repeat(15) { huDraw(load(id)).getOrThrow() }
            assertThat(load(id).uiRounds).hasSize(16)
            assertThat(load(id).isEnded).isFalse()
            huDiscard(load(id), HuData(8, EAST, SOUTH)).getOrThrow()
            val game = load(id)
            assertThat(game.uiRounds).hasSize(16)
            assertThat(game.isEnded).isTrue()
            assertThat(game.ongoingRound.isOngoing()).isFalse()
        }
    }

    @Nested
    inner class Penalties {

        @Test
        fun `non divided penalty only affects the penalized player and accumulates`() = runTest {
            val id = newGame()
            setPenalty(load(id).ongoingRound, PenaltyData(10, isDivided = false, penalizedPlayerInitialSeat = NORTH)).getOrThrow()
            setPenalty(load(id).ongoingRound, PenaltyData(5, isDivided = false, penalizedPlayerInitialSeat = NORTH)).getOrThrow()
            with(load(id).ongoingRound) {
                assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(0, 0, 0, -15).inOrder()
            }
        }

        @Test
        fun `divided penalty gives a third (integer division) to the others`() = runTest {
            val id = newGame()
            setPenalty(load(id).ongoingRound, PenaltyData(10, isDivided = true, penalizedPlayerInitialSeat = EAST)).getOrThrow()
            with(load(id).ongoingRound) {
                // 10 / 3 = 3: the round is not zero sum, as in the current implementation.
                assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(-10, 3, 3, 3).inOrder()
            }
        }

        @Test
        fun `cancel penalties resets the four penalties`() = runTest {
            val id = newGame()
            setPenalty(load(id).ongoingRound, PenaltyData(30, isDivided = true, penalizedPlayerInitialSeat = WEST)).getOrThrow()
            cancelPenalties(load(id).ongoingRound).getOrThrow()
            assertThat(load(id).ongoingRound.areTherePenalties()).isFalse()
        }
    }

    @Nested
    inner class Rounds {

        @Test
        fun `deleting a finished round removes it and renumbers the rest`() = runTest {
            val id = newGame()
            huSelfPick(load(id), HuData(8, EAST)).getOrThrow()
            huSelfPick(load(id), HuData(16, SOUTH)).getOrThrow()
            val firstRoundId = load(id).uiRounds[0].roundId
            deleteRound(firstRoundId).getOrThrow()
            val game = load(id)
            assertThat(game.uiRounds).hasSize(2)
            assertThat(game.uiRounds[0].winnerInitialSeat).isEqualTo(SOUTH)
            assertThat(game.uiRounds[0].roundNumber).isEqualTo(1)
            assertThat(game.ongoingRound.isOngoing()).isTrue()
        }

        @Test
        fun `deleting the ongoing round recreates a clean ongoing round`() = runTest {
            val id = newGame()
            setPenalty(load(id).ongoingRound, PenaltyData(10, false, EAST)).getOrThrow()
            val ongoingId = load(id).ongoingRound.roundId
            deleteRound(ongoingId).getOrThrow()
            val game = load(id)
            assertThat(game.uiRounds).hasSize(1)
            assertThat(game.ongoingRound.roundId).isNotEqualTo(ongoingId)
            assertThat(game.ongoingRound.areTherePenalties()).isFalse()
        }

        @Test
        fun `deleting an unknown round fails`() = runTest {
            assertThat(deleteRound(12345).isFailure).isTrue()
        }
    }

    @Nested
    inner class GameLifecycle {

        @Test
        fun `end game sets the end date keeping everything else`() = runTest {
            val id = newGame()
            endGame(load(id)).getOrThrow()
            val game = load(id)
            assertThat(game.isEnded).isTrue()
            assertThat(game.gameName).isEqualTo("Game")
            assertThat(game.uiRounds).hasSize(1)
        }

        @Test
        fun `resume removes the end date and adds an ongoing round when the last one is finished`() = runTest {
            val id = newGame()
            huDraw(load(id)).getOrThrow()
            // Simulate a game ended after a finished last round.
            rounds.deleteOne(load(id).ongoingRound.roundId).getOrThrow()
            endGame(load(id)).getOrThrow()
            assertThat(load(id).ongoingRound.isOngoing()).isFalse()

            resumeGame(load(id)).getOrThrow()
            val game = load(id)
            assertThat(game.isEnded).isFalse()
            assertThat(game.uiRounds).hasSize(2)
            assertThat(game.ongoingRound.isOngoing()).isTrue()
        }

        @Test
        fun `resume keeps the existing ongoing round`() = runTest {
            val id = newGame()
            endGame(load(id)).getOrThrow()
            assertThat(resumeGame(load(id)).getOrThrow()).isFalse()
            assertThat(load(id).uiRounds).hasSize(1)
            assertThat(load(id).isEnded).isFalse()
        }

        @Test
        fun `a 16 round game cannot be resumed`() = runTest {
            val id = newGame()
            repeat(16) { huDraw(load(id)).getOrThrow() }
            val result = resumeGame(load(id))
            assertThat(result.exceptionOrNull()).isInstanceOf(GameRoundsNumberExceededException::class.java)
            assertThat(load(id).isEnded).isTrue()
        }

        @Test
        fun `edit names normalizes and falls back to the start date for blank game names`() = runTest {
            val id = newGame()
            editNames(load(id), "  ", " \"Ana\" ", "B", "C", "D ").getOrThrow()
            val game = load(id)
            assertThat(game.playersNames.toList()).containsExactly("Ana", "B", "C", "D").inOrder()
            assertThat(game.gameName).matches("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")
            editNames(load(id), " New \"name\" ", "A", "B", "C", "D").getOrThrow()
            assertThat(load(id).gameName).isEqualTo("New name")
        }

        @Test
        fun `delete game removes the game and its rounds only`() = runTest {
            val a = newGame()
            val b = newGame()
            huDraw(load(a)).getOrThrow()
            deleteGame(a).getOrThrow()
            assertThat(games.games.value.map { it.gameId }).containsExactly(b)
            assertThat(rounds.rounds.value.map { it.gameId }.distinct()).containsExactly(b)
        }
    }

    @Nested
    inner class Queries {

        @Test
        fun `all games are listed newest first and games without rounds are skipped`() = runTest {
            GameScenarios.handWritten.forEach { s ->
                games.insertOne(s.game).getOrThrow()
                s.rounds.forEach { rounds.insertOne(it.clone()).getOrThrow() }
            }
            val all = getAllGames().first()
            assertThat(all.map { it.gameId }).isEqualTo(GameScenarios.handWritten.sortedByDescending { it.game.startDate }.map { it.game.gameId })
        }

        @Test
        fun `a game without rounds hides the whole list (current behaviour)`() = runTest {
            newGame()
            games.insertOne(GameScenarios.newGame.game.copy(gameId = 50)).getOrThrow()
            // GetAllGamesFlowUseCase returns null for the whole emission if any game has no rounds.
            val emissions = mutableListOf<List<UiGame>>()
            val job = backgroundScope.launch { getAllGames().collect { emissions += it } }
            testScheduler.advanceUntilIdle()
            assertThat(emissions).isEmpty()
            job.cancel()
        }
    }

    @Nested
    inner class Import {

        private fun importer() = ImportGamesFromJsonUseCase(games, rounds, deleteGame)

        @Test
        fun `imports several games keeping names, dates and rounds`() = runTest {
            val json = """[{"gameName":"g1","startDate":1000,"endDate":2000,"nameP1":"A","nameP2":"B","nameP3":"C","nameP4":"D",
                "rounds":[{"roundNumber":1,"winnerInitialSeat":0,"discarderInitialSeat":1,"handPoints":8,"pointsP1":0,"pointsP2":0,"pointsP3":0,"pointsP4":0,"penaltyP1":0,"penaltyP2":0,"penaltyP3":0,"penaltyP4":0},
                           {"roundNumber":2,"winnerInitialSeat":null,"discarderInitialSeat":null,"handPoints":0,"pointsP1":0,"pointsP2":0,"pointsP3":0,"pointsP4":0,"penaltyP1":-5,"penaltyP2":0,"penaltyP3":0,"penaltyP4":0}]},
                {"gameName":"g2","startDate":3000,"endDate":null,"nameP1":"E","nameP2":"F","nameP3":"G","nameP4":"H","rounds":[{"roundNumber":1,"winnerInitialSeat":-1,"discarderInitialSeat":-1,"handPoints":0,"pointsP1":0,"pointsP2":0,"pointsP3":0,"pointsP4":0,"penaltyP1":0,"penaltyP2":0,"penaltyP3":0,"penaltyP4":0}]}]"""
            val ids = importer().createGamesInDbFrom(json)
            assertThat(ids).hasSize(2)
            val g1 = load(ids[0])
            assertThat(g1.gameName).isEqualTo("g1")
            assertThat(g1.startDate.time).isEqualTo(1000)
            assertThat(g1.endDate?.time).isEqualTo(2000)
            assertThat(g1.uiRounds.map { it.winnerInitialSeat }).containsExactly(EAST, null).inOrder()
            // Points in the file are ignored: they are recomputed from the inputs.
            assertThat(g1.uiRounds[0].pointsP1).isEqualTo(32)
            assertThat(g1.ongoingRound.penaltyP1).isEqualTo(-5)
            val g2 = load(ids[1])
            assertThat(g2.endDate).isNull()
            assertThat(g2.uiRounds.single().winnerInitialSeat).isEqualTo(NONE)
        }

        @Test
        fun `invalid json throws and imports nothing`() = runTest {
            val result = runCatching { importer().createGamesInDbFrom("{not json") }
            assertThat(result.exceptionOrNull()).isInstanceOf(SerializationException::class.java)
            assertThat(games.games.value).isEmpty()
        }

        @Test
        fun `unknown keys are rejected`() = runTest {
            val json = """[{"gameName":"g","startDate":1,"endDate":null,"nameP1":"A","nameP2":"B","nameP3":"C","nameP4":"D","extra":1}]"""
            assertThat(runCatching { importer().createGamesInDbFrom(json) }.isFailure).isTrue()
        }

        @Test
        fun `a game without rounds is imported as a game with no rounds`() = runTest {
            val json = """[{"gameName":"g","startDate":1,"endDate":null,"nameP1":"A","nameP2":"B","nameP3":"C","nameP4":"D"}]"""
            val ids = importer().createGamesInDbFrom(json)
            assertThat(games.games.value.map { it.gameId }).containsExactlyElementsIn(ids)
            assertThat(rounds.rounds.value).isEmpty()
        }
    }
}
