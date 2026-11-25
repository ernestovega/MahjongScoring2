package com.etologic.mahjongscoring2.app.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.app.screens.game.GameFragment.GamePages
import com.etologic.mahjongscoring2.app.screens.game.GamePageActions
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.use_cases.CancelAllPenaltiesUseCase
import com.etologic.mahjongscoring2.business.use_cases.DeleteRoundUseCase
import com.etologic.mahjongscoring2.business.use_cases.EditGameNamesUseCase
import com.etologic.mahjongscoring2.business.use_cases.EndGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.EndRoundUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDiscardUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuSelfPickUseCase
import com.etologic.mahjongscoring2.business.use_cases.ResumeGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.SetPenaltyUseCase
import com.etologic.mahjongscoring2.business.use_cases.ShowInAppReviewUseCase
import com.etologic.mahjongscoring2.data_source.local_data_sources.datastore.dataStore
import com.etologic.mahjongscoring2.data_source.repositories.DiffCalcsFeatureEnabledRepository
import com.etologic.mahjongscoring2.data_source.repositories.InAppReviewRepository
import com.etologic.mahjongscoring2.data_source.repositories.SelectedSeatsOrientationRepository
import com.etologic.mahjongscoring2.testutil.FakeGamesRepository
import com.etologic.mahjongscoring2.testutil.FakeRoundsRepository
import com.etologic.mahjongscoring2.testutil.GameScenario
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.etologic.mahjongscoring2.testutil.Goldens
import com.etologic.mahjongscoring2.testutil.MainDispatcherRule
import com.etologic.mahjongscoring2.testutil.awaitFirst
import com.etologic.mahjongscoring2.testutil.clone
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var games: FakeGamesRepository
    private lateinit var rounds: FakeRoundsRepository
    private lateinit var seatsRepository: SelectedSeatsOrientationRepository
    private lateinit var diffsRepository: DiffCalcsFeatureEnabledRepository
    private lateinit var viewModel: GameViewModel

    @Before
    fun setUp() = runBlocking<Unit> {
        context.dataStore.edit { it.clear() }
        games = FakeGamesRepository()
        rounds = FakeRoundsRepository()
        seatsRepository = SelectedSeatsOrientationRepository(context)
        diffsRepository = DiffCalcsFeatureEnabledRepository(context)
        val getOneGame = GetOneGameFlowUseCase(games, rounds)
        val endGame = EndGameUseCase(games)
        val endRound = EndRoundUseCase(rounds, endGame, getOneGame)
        viewModel = GameViewModel(
            getOneGameFlowUseCase = getOneGame,
            editGameNamesUseCase = EditGameNamesUseCase(games),
            huDiscardUseCase = HuDiscardUseCase(rounds, endRound),
            huSelfPickUseCase = HuSelfPickUseCase(rounds, endRound),
            huDrawUseCase = HuDrawUseCase(rounds, endRound),
            setPenaltyUseCase = SetPenaltyUseCase(rounds),
            cancelAllPenaltiesUseCase = CancelAllPenaltiesUseCase(rounds),
            deleteRoundUseCase = DeleteRoundUseCase(rounds),
            resumeGameUseCase = ResumeGameUseCase(games, rounds),
            endGameUseCase = endGame,
            exportGameToTextUseCase = ExportGameToTextUseCase(getOneGame),
            exportGameToCsvUseCase = ExportGameToCsvUseCase(getOneGame),
            exportGameToJsonUseCase = ExportGameToJsonUseCase(getOneGame),
            diffCalcsFeatureEnabledRepository = diffsRepository,
            selectedSeatsOrientationRepository = seatsRepository,
            showInAppReviewUseCase = ShowInAppReviewUseCase(InAppReviewRepository(context)),
        )
    }

    private fun seed(scenario: GameScenario) = runBlocking {
        games.insertOne(scenario.game).getOrThrow()
        scenario.rounds.forEach { rounds.insertOne(it.clone()).getOrThrow() }
    }

    private fun open(scenario: GameScenario): GameUiState.Loaded {
        seed(scenario)
        viewModel.setGameId(scenario.game.gameId)
        return awaitLoaded { it.game.gameId == scenario.game.gameId }
    }

    private fun awaitLoaded(predicate: (GameUiState.Loaded) -> Boolean): GameUiState.Loaded =
        viewModel.gameUiStateFlow.awaitFirst { it is GameUiState.Loaded && predicate(it) } as GameUiState.Loaded

    @Test
    fun `initial state is loading until a game id is set`() {
        assertThat(viewModel.gameUiStateFlow.value).isEqualTo(GameUiState.Loading)
    }

    @Test
    fun `loaded state defaults`() {
        val state = open(GameScenarios.penalties)
        assertThat(state.error).isNull()
        assertThat(state.isDiffsCalcsFeatureEnabled).isTrue()
        assertThat(state.shouldShowDiffs).isFalse()
        assertThat(state.seatsArrangement).isEqualTo(SeatsArrangement.DOWN)
        assertThat(state.pageToShow).isEqualTo(GamePages.TABLE to GamePageActions.NOTHING)
        assertThat(state.selectedSeat).isEqualTo(TableWinds.NONE)
        assertThat(state.game.uiRounds).hasSize(5)
    }

    @Test
    fun `seat selection`() {
        open(GameScenarios.newGame)
        viewModel.onSeatClicked(TableWinds.SOUTH)
        awaitLoaded { it.selectedSeat == TableWinds.SOUTH }
        viewModel.unselectSeats()
        awaitLoaded { it.selectedSeat == TableWinds.NONE }
    }

    @Test
    fun `saving a hand shows the list highlighting the last round`() {
        open(GameScenarios.newGame)
        viewModel.saveHuDiscardRound(HuData(points = 8, winnerInitialSeat = TableWinds.EAST, discarderInitialSeat = TableWinds.WEST))
        val state = awaitLoaded { it.game.uiRounds.size == 2 && it.pageToShow.first == GamePages.LIST }
        assertThat(state.pageToShow.second).isEqualTo(GamePageActions.HIGHLIGHT_LAST_ROUND)
        assertThat(state.game.uiRounds[0].totalPointsP1).isEqualTo(32)
        assertThat(state.game.uiRounds[0].totalPointsP3).isEqualTo(-16)
    }

    @Test
    fun `self pick and draw`() {
        open(GameScenarios.newGame)
        viewModel.saveHuSelfPickRound(HuData(points = 10, winnerInitialSeat = TableWinds.NORTH))
        awaitLoaded { it.game.uiRounds.size == 2 }
        viewModel.saveDrawRound()
        val state = awaitLoaded { it.game.uiRounds.size == 3 }
        assertThat(state.game.uiRounds.map { it.winnerInitialSeat }).containsExactly(TableWinds.NORTH, TableWinds.NONE, null).inOrder()
        assertThat(state.game.ongoingRound.totalPointsP4).isEqualTo(54)
    }

    @Test
    fun `penalties are set on the ongoing round and can be cancelled`() {
        open(GameScenarios.newGame)
        viewModel.savePenalty(PenaltyData(points = 30, isDivided = true, penalizedPlayerInitialSeat = TableWinds.WEST))
        val state = awaitLoaded { it.game.ongoingRound.areTherePenalties() }
        with(state.game.ongoingRound) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(10, 10, -30, 10).inOrder()
        }
        viewModel.cancelPenalties()
        awaitLoaded { !it.game.ongoingRound.areTherePenalties() }
    }

    @Test
    fun `remove round`() {
        val state = open(GameScenarios.selfPicks)
        viewModel.removeRound(state.game.uiRounds[0].roundId)
        val updated = awaitLoaded { it.game.uiRounds.size == 4 }
        assertThat(updated.game.uiRounds[0].winnerInitialSeat).isEqualTo(TableWinds.SOUTH)
    }

    @Test
    fun `edit names`() {
        val state = open(GameScenarios.newGame)
        viewModel.saveGameNames(state.game, "Renamed", "W", "X", "Y", "Z")
        val updated = awaitLoaded { it.game.gameName == "Renamed" }
        assertThat(updated.game.playersNames.toList()).containsExactly("W", "X", "Y", "Z").inOrder()
    }

    @Test
    fun `ending a game and asking for the list shows the ranking instead`() {
        open(GameScenarios.newGame)
        viewModel.endGame()
        awaitLoaded { it.game.isEnded }
        viewModel.showPage(GamePages.LIST, GamePageActions.HIGHLIGHT_LAST_ROUND)
        val state = awaitLoaded { it.pageToShow.second == GamePageActions.SHOW_RANKING }
        assertThat(state.pageToShow.first).isEqualTo(GamePages.TABLE)
        // Showing the ranking switches the persisted seats arrangement to RANKING.
        assertThat(state.seatsArrangement).isEqualTo(SeatsArrangement.RANKING)
        assertThat(seatsRepository.selectedSeatsArrangementFlow.awaitFirst { true }).isEqualTo(SeatsArrangement.RANKING)
    }

    @Test
    fun `the 16th hand ends the game and shows the ranking`() {
        open(GameScenarios.newGame)
        repeat(15) { index ->
            viewModel.saveDrawRound()
            awaitLoaded { it.game.uiRounds.size == index + 2 }
        }
        viewModel.saveHuSelfPickRound(HuData(points = 8, winnerInitialSeat = TableWinds.EAST))
        val state = awaitLoaded { it.game.isEnded && it.pageToShow.second == GamePageActions.SHOW_RANKING }
        assertThat(state.game.uiRounds).hasSize(16)
    }

    @Test
    fun `resume an ended game`() {
        open(GameScenarios.allDiscardPairs)
        viewModel.resumeGame()
        val state = awaitLoaded { !it.game.isEnded }
        assertThat(state.game.uiRounds).hasSize(13)
        assertThat(state.game.ongoingRound.isOngoing()).isTrue()
    }

    @Test
    fun `seats arrangement cycles DOWN, OUT, RANKING`() {
        open(GameScenarios.newGame)
        viewModel.nextSeatsOrientation()
        awaitLoaded { it.seatsArrangement == SeatsArrangement.OUT }
        viewModel.nextSeatsOrientation()
        awaitLoaded { it.seatsArrangement == SeatsArrangement.RANKING }
        viewModel.nextSeatsOrientation()
        awaitLoaded { it.seatsArrangement == SeatsArrangement.DOWN }
    }

    @Test
    fun `diffs can only be shown while the feature is enabled`() {
        open(GameScenarios.newGame)
        viewModel.toggleDiffsView(true)
        awaitLoaded { it.shouldShowDiffs }
        viewModel.toggleDiffsView(false)
        awaitLoaded { !it.shouldShowDiffs }
        viewModel.hideDiffCalcsFeature()
        awaitLoaded { !it.isDiffsCalcsFeatureEnabled }
        viewModel.toggleDiffsView(true)
        Thread.sleep(200)
        assertThat((viewModel.gameUiStateFlow.value as GameUiState.Loaded).shouldShowDiffs).isFalse()
        viewModel.showDiffCalcsFeature()
        awaitLoaded { it.isDiffsCalcsFeatureEnabled }
    }

    @Test
    fun `errors are exposed as error state`() {
        open(GameScenarios.newGame)
        rounds.failure = IllegalStateException("disk full")
        viewModel.savePenalty(PenaltyData(10, false, TableWinds.EAST))
        val state = viewModel.gameUiStateFlow.awaitFirst { it is GameUiState.Error } as GameUiState.Error
        assertThat(state.throwable).hasMessageThat().isEqualTo("disk full")
    }

    @Test
    fun `share as text, csv and json produce the golden exports`() {
        open(GameScenarios.fullGame)
        val dir = "scenarios/${GameScenarios.fullGame.name}"

        val text = CompletableDeferred<String>()
        viewModel.shareGame(ShareGameOptions.TEXT, tempFolder.root, { text.complete(it) }, { })
        assertThat(await(text)).isEqualTo(Goldens.read("$dir/export.txt"))

        val csv = CompletableDeferred<Array<File>>()
        viewModel.shareGame(ShareGameOptions.CSV, tempFolder.root, { }, { csv.complete(it) })
        assertThat(await(csv).single().readText()).isEqualTo(Goldens.read("$dir/export.csv"))

        val json = CompletableDeferred<Array<File>>()
        viewModel.shareGame(ShareGameOptions.JSON, tempFolder.root, { }, { json.complete(it) })
        assertThat(await(json).single().readText()).isEqualTo(Goldens.read("$dir/game.json"))
    }

    private fun <T> await(deferred: CompletableDeferred<T>): T = runBlocking { withTimeout(5_000) { deferred.await() } }
}
