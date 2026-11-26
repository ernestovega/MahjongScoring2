package com.etologic.mahjongscoring2.golden

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.use_cases.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGamesToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring2.testutil.FakeGamesRepository
import com.etologic.mahjongscoring2.testutil.FakeRoundsRepository
import com.etologic.mahjongscoring2.testutil.GameReport
import com.etologic.mahjongscoring2.testutil.GameScenario
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.etologic.mahjongscoring2.testutil.Goldens
import com.etologic.mahjongscoring2.testutil.clone
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Golden ("approval") tests over every scenario in [GameScenarios].
 *
 * For each scenario, under `src/test/resources/golden/scenarios/<name>/`:
 *  - `game.json`   the game in the app's portable export format (also the input for other implementations)
 *  - `report.txt`  every value derived by the business layer (points, totals, seats, diffs, ranking...)
 *  - `export.csv`  the "share as table" export
 *  - `export.txt`  the "share as plain text" export
 *
 * The scenario is also re-imported from its `game.json`, proving that import + scoring reproduces the
 * same report: a KMP port only needs to read `game.json` and compare with `report.txt`.
 */
class GameScenariosGoldenTest {

    private class Env {
        val games = FakeGamesRepository()
        val rounds = FakeRoundsRepository()
        val getOneGame = GetOneGameFlowUseCase(games, rounds)
        val getAllGames = GetAllGamesFlowUseCase(games, rounds)

        fun seed(vararg scenarios: GameScenario) = runBlocking {
            scenarios.forEach { scenario ->
                games.insertOne(scenario.game).getOrThrow()
                scenario.rounds.forEach { rounds.insertOne(it.clone()).getOrThrow() }
            }
        }

        fun load(gameId: GameId): UiGame = runBlocking { getOneGame(gameId).first() }
    }

    @TestFactory
    fun scenarios(): List<DynamicNode> = GameScenarios.all.map { scenario ->
        val dir = "scenarios/${scenario.name}"
        DynamicContainer.dynamicContainer(
            scenario.name,
            listOf(
                dynamicTest("report") {
                    val env = Env().apply { seed(scenario) }
                    val uiGame = env.load(scenario.game.gameId)
                    val report = GameReport.render(uiGame)
                    // The pure mapper and the flow based use case must agree.
                    assertThat(GameReport.render(scenario.toUiGame())).isEqualTo(report)
                    Goldens.assertMatches("$dir/report.txt", report)
                },
                dynamicTest("json export") {
                    val env = Env().apply { seed(scenario) }
                    val json = ExportGameToJsonUseCase(env.getOneGame).jsonFrom(env.load(scenario.game.gameId))
                    Goldens.assertMatches("$dir/game.json", json)
                },
                dynamicTest("csv export") {
                    val env = Env().apply { seed(scenario) }
                    val csv = ExportGameToCsvUseCase(env.getOneGame).buildCsvText(env.load(scenario.game.gameId))
                    Goldens.assertMatches("$dir/export.csv", csv)
                },
                dynamicTest("text export") {
                    val env = Env().apply { seed(scenario) }
                    val text = runBlocking { ExportGameToTextUseCase(env.getOneGame)(scenario.game.gameId).getOrThrow() }
                    Goldens.assertMatches("$dir/export.txt", text)
                },
                dynamicTest("import of game.json reproduces report") {
                    val goldenJson = Goldens.file("$dir/game.json")
                    if (!goldenJson.exists()) return@dynamicTest // created by the "json export" test
                    val env = Env()
                    val importer = ImportGamesFromJsonUseCase(env.games, env.rounds, DeleteGameUseCase(env.games, env.rounds))
                    val ids = runBlocking { importer.createGamesInDbFrom(goldenJson.readText()) }
                    assertThat(ids).hasSize(1)
                    val reimported = env.load(ids.single())
                    assertThat(GameReport.render(reimported)).isEqualTo(Goldens.read("$dir/report.txt"))
                },
            )
        )
    }

    @Test
    fun `scenario index`() {
        val index = buildString {
            appendLine("# Golden game scenarios")
            appendLine()
            appendLine("Generated from GameScenarios.kt. Each folder holds game.json (input, portable format),")
            appendLine("report.txt (all derived values), export.csv and export.txt.")
            appendLine()
            GameScenarios.all.forEach { appendLine("- ${it.name}: ${it.description}") }
        }
        Goldens.assertMatches("scenarios/INDEX.md", index)
    }

    @Test
    fun `export of all games to a single json`() {
        val env = Env().apply { seed(*GameScenarios.handWritten.toTypedArray()) }
        val uiGames = runBlocking { env.getAllGames().first() }
        // Games are listed newest first.
        assertThat(uiGames.map { it.gameId }).isEqualTo(GameScenarios.handWritten.sortedByDescending { it.game.startDate }.map { it.game.gameId })
        val json = ExportGamesToJsonUseCase(env.getAllGames).jsonFrom(uiGames)
        Goldens.assertMatches("all_games_export.json", json)

        // Re-importing the full database export recreates the same games.
        val target = Env()
        val importer = ImportGamesFromJsonUseCase(target.games, target.rounds, DeleteGameUseCase(target.games, target.rounds))
        val ids = runBlocking { importer.createGamesInDbFrom(json) }
        assertThat(ids).hasSize(uiGames.size)
        val reports = ids.map { GameReport.render(target.load(it)) }
        assertThat(reports).containsExactlyElementsIn(uiGames.map { GameReport.render(it) }).inOrder()
    }

    @Test
    fun `exported file names`(@TempDir tempDir: File) {
        val env = Env().apply { seed(*GameScenarios.all.toTypedArray()) }
        val text = buildString {
            GameScenarios.all.forEach { scenario ->
                val id = scenario.game.gameId
                val csv = runBlocking { ExportGameToCsvUseCase(env.getOneGame)(id, tempDir).getOrThrow() }
                val json = runBlocking { ExportGameToJsonUseCase(env.getOneGame)(id, tempDir).getOrThrow() }
                assertThat(csv.readText()).isEqualTo(Goldens.read("scenarios/${scenario.name}/export.csv"))
                assertThat(json.readText()).isEqualTo(Goldens.read("scenarios/${scenario.name}/game.json"))
                appendLine("${scenario.name}: ${csv.name} | ${json.name}")
            }
            val all = runBlocking { ExportGamesToJsonUseCase(env.getAllGames)(tempDir).getOrThrow() }
            appendLine("all games: ${all.joinToString { it.name }}")
        }
        Goldens.assertMatches("export_file_names.txt", text)
    }
}
