package com.etologic.mahjongscoring2.app.viewmodels

import androidx.lifecycle.SavedStateHandle
import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.app.model.Diff
import com.etologic.mahjongscoring2.app.screens.MainViewModel
import com.etologic.mahjongscoring2.app.screens.combinations.CombinationsUiState
import com.etologic.mahjongscoring2.app.screens.combinations.CombinationsViewModel
import com.etologic.mahjongscoring2.app.screens.diffs_calculator.DiffsCalculatorUiState
import com.etologic.mahjongscoring2.app.screens.diffs_calculator.DiffsCalculatorViewModel
import com.etologic.mahjongscoring2.app.screens.old_games.OldGamesUiState
import com.etologic.mahjongscoring2.app.screens.old_games.OldGamesViewModel
import com.etologic.mahjongscoring2.app.utils.ENGLISH
import com.etologic.mahjongscoring2.app.utils.SPANISH
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.use_cases.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGamesToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring2.data_source.local_data_sources.datastore.dataStore
import com.etologic.mahjongscoring2.data_source.repositories.CombinationsRepository
import com.etologic.mahjongscoring2.data_source.repositories.LanguageRepository
import com.etologic.mahjongscoring2.testutil.FakeGamesRepository
import com.etologic.mahjongscoring2.testutil.FakeRoundsRepository
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
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class OtherViewModelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() = runBlocking<Unit> {
        withTimeout(10_000) { context.dataStore.edit { it.clear() } }
        Locale.setDefault(Locale.US)
    }

    private fun <T> await(deferred: CompletableDeferred<T>): T = runBlocking { withTimeout(5_000) { deferred.await() } }

    // region MainViewModel

    @Test
    fun `main view model keeps the active game id`() {
        val viewModel = MainViewModel(SavedStateHandle())
        assertThat(viewModel.activeGameId.value).isEqualTo(UiGame.NOT_SET_GAME_ID)
        viewModel.updateActiveGameId(7)
        assertThat(viewModel.activeGameId.value).isEqualTo(7)
    }

    // endregion

    // region DiffsCalculatorViewModel

    @Test
    fun `diffs calculator starts at 32 with 200 rows and grows up to 2000 rows`() {
        val viewModel = DiffsCalculatorViewModel()
        val first = viewModel.diffsCalculatorUiState.awaitFirst { it is DiffsCalculatorUiState.Loaded } as DiffsCalculatorUiState.Loaded
        assertThat(first.diffsList).hasSize(200)
        assertThat(first.diffsList.first()).isEqualTo(Diff(32))
        assertThat(first.diffsList.last()).isEqualTo(Diff(231))
        repeat(12) { viewModel.loadNextInterval() }
        val last = viewModel.diffsCalculatorUiState.awaitFirst { (it as? DiffsCalculatorUiState.Loaded)?.diffsList?.size == 2000 }
                as DiffsCalculatorUiState.Loaded
        assertThat(last.diffsList.map { it.pointsNeeded }).isEqualTo((32 until 2032).toList())
        assertThat(DiffsCalculatorViewModel.MAX_ITEMS).isEqualTo(2000)
    }

    // endregion

    // region CombinationsViewModel

    private fun combinationsViewModel(language: String = ENGLISH): CombinationsViewModel {
        val languageRepository = LanguageRepository(context)
        runBlocking { languageRepository.save(language) }
        return CombinationsViewModel(CombinationsRepository(context, languageRepository))
    }

    private fun CombinationsViewModel.awaitLoaded(predicate: (List<String>) -> Boolean): List<String> =
        (combinationsUiState.awaitFirst { it is CombinationsUiState.Loaded && predicate(it.combinationsList.map { c -> c.combinationName }) }
                as CombinationsUiState.Loaded).combinationsList.map { it.combinationName }

    @Test
    fun `combinations are all listed and can be filtered by name, description or points`() {
        val viewModel = combinationsViewModel()
        assertThat(viewModel.combinationsUiState.value).isEqualTo(CombinationsUiState.Loading)
        assertThat(viewModel.awaitLoaded { it.size == 81 }).hasSize(81)

        viewModel.searchCombination("dragon")
        assertThat(viewModel.awaitLoaded { it.size < 81 }).containsExactly(
            "Dragon pung", "Two dragon pungs", "Little three dragons", "Big three dragons",
        ).inOrder()

        viewModel.searchCombination("88")
        assertThat(viewModel.awaitLoaded { it.size == 7 }).hasSize(7)

        viewModel.searchCombination("KONG")
        assertThat(viewModel.awaitLoaded { it.none { name -> name.startsWith("Dragon") } }).isNotEmpty()

        viewModel.searchCombination("zzzz")
        assertThat(viewModel.combinationsUiState.awaitFirst { it is CombinationsUiState.Empty }).isEqualTo(CombinationsUiState.Empty)

        viewModel.searchCombination("")
        assertThat(viewModel.awaitLoaded { it.size == 81 }).hasSize(81)
    }

    @Test
    fun `combinations search works on the localized names`() {
        val viewModel = combinationsViewModel(SPANISH)
        viewModel.searchCombination("dragones")
        assertThat(viewModel.awaitLoaded { it.size < 81 }).containsExactly(
            "Dos pungs de dragones", "Tres pequeños dragones", "Tres grandes dragones",
        ).inOrder()
        Locale.setDefault(Locale.US)
    }

    // endregion

    // region OldGamesViewModel

    private class OldGamesEnv {
        val games = FakeGamesRepository()
        val rounds = FakeRoundsRepository()
        private val getOne = GetOneGameFlowUseCase(games, rounds)
        private val getAll = GetAllGamesFlowUseCase(games, rounds)
        val viewModel = OldGamesViewModel(
            getAllGamesFlowUseCase = getAll,
            createGameUseCase = CreateGameUseCase(games, rounds),
            deleteGameUseCase = DeleteGameUseCase(games, rounds),
            exportGameToTextUseCase = ExportGameToTextUseCase(getOne),
            exportGameToCsvUseCase = ExportGameToCsvUseCase(getOne),
            exportGameToJsonUseCase = ExportGameToJsonUseCase(getOne),
            exportGamesToJsonUseCase = ExportGamesToJsonUseCase(getAll),
            importGamesFromJsonUseCase = ImportGamesFromJsonUseCase(games, rounds, DeleteGameUseCase(games, rounds)),
        )

        fun seedHandWritten() = runBlocking {
            GameScenarios.handWritten.forEach { s ->
                games.insertOne(s.game).getOrThrow()
                s.rounds.forEach { rounds.insertOne(it.clone()).getOrThrow() }
            }
        }

        fun awaitGames(predicate: (List<UiGame>) -> Boolean): List<UiGame> =
            (viewModel.oldGamesUiStateFlow.awaitFirst { it is OldGamesUiState.Loaded && predicate(it.oldGamesList) }
                    as OldGamesUiState.Loaded).oldGamesList
    }

    @Test
    fun `old games are listed newest first`() {
        val env = OldGamesEnv().apply { seedHandWritten() }
        assertThat(env.viewModel.oldGamesUiStateFlow.value).isEqualTo(OldGamesUiState.Loading)
        val list = env.awaitGames { it.isNotEmpty() }
        assertThat(list.map { it.gameId }).isEqualTo(GameScenarios.handWritten.sortedByDescending { it.game.startDate }.map { it.game.gameId })
    }

    @Test
    fun `create and delete games`() {
        val env = OldGamesEnv()
        assertThat(env.awaitGames { true }).isEmpty()
        val created = CompletableDeferred<GameId>()
        env.viewModel.createGame("New", "A", "B", "C", "D") { created.complete(it) }
        val id = await(created)
        assertThat(env.awaitGames { it.size == 1 }.single().gameId).isEqualTo(id)
        env.viewModel.deleteGame(id)
        assertThat(env.awaitGames { it.isEmpty() }).isEmpty()
    }

    @Test
    fun `errors are exposed as error state`() {
        val env = OldGamesEnv()
        env.awaitGames { true }
        env.games.failure = IllegalStateException("boom")
        env.viewModel.createGame("New", "A", "B", "C", "D") { }
        val error = env.viewModel.oldGamesUiStateFlow.awaitFirst { it is OldGamesUiState.Error } as OldGamesUiState.Error
        assertThat(error.throwable).hasMessageThat().isEqualTo("boom")
    }

    @Test
    fun `share a game in the three formats`() {
        val env = OldGamesEnv().apply { seedHandWritten() }
        val scenario = GameScenarios.specialNames
        val dir = "scenarios/${scenario.name}"

        val text = CompletableDeferred<String>()
        env.viewModel.shareGame(scenario.game.gameId, ShareGameOptions.TEXT, tempFolder.root, { text.complete(it) }, {})
        assertThat(await(text)).isEqualTo(Goldens.read("$dir/export.txt"))

        val csv = CompletableDeferred<Array<File>>()
        env.viewModel.shareGame(scenario.game.gameId, ShareGameOptions.CSV, tempFolder.root, {}, { csv.complete(it) })
        assertThat(await(csv).single().readText()).isEqualTo(Goldens.read("$dir/export.csv"))

        val json = CompletableDeferred<Array<File>>()
        env.viewModel.shareGame(scenario.game.gameId, ShareGameOptions.JSON, tempFolder.root, {}, { json.complete(it) })
        assertThat(await(json).single().readText()).isEqualTo(Goldens.read("$dir/game.json"))
    }

    @Test
    fun `export all games and import them back`() {
        val source = OldGamesEnv().apply { seedHandWritten() }
        source.awaitGames { it.isNotEmpty() }
        val exported = CompletableDeferred<Array<File>>()
        source.viewModel.exportGames(tempFolder.root) { exported.complete(it) }
        val file = await(exported).single()
        assertThat(file.name).isEqualTo("MahjongMadrid2_DataBase.json")
        assertThat(file.readText()).isEqualTo(Goldens.read("all_games_export.json"))

        val target = OldGamesEnv()
        target.viewModel.importGames(Uri.fromFile(file)) { context.contentResolver }
        val imported = target.awaitGames { it.size == GameScenarios.handWritten.size }
        assertThat(imported.map { it.gameName }).isEqualTo(source.awaitGames { true }.map { it.gameName })
    }

    // endregion
}
