package com.etologic.mahjongscoring2.data_source.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.utils.CHINESE
import com.etologic.mahjongscoring2.app.utils.DUTCH
import com.etologic.mahjongscoring2.app.utils.ENGLISH
import com.etologic.mahjongscoring2.app.utils.FRENCH
import com.etologic.mahjongscoring2.app.utils.ITALIAN
import com.etologic.mahjongscoring2.app.utils.LanguageHelper
import com.etologic.mahjongscoring2.app.utils.SPANISH
import com.etologic.mahjongscoring2.app.utils.setLocale
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.data_source.local_data_sources.datastore.dataStore
import com.etologic.mahjongscoring2.testutil.Goldens
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

/** DataStore backed settings and the localized MCR combinations catalogue. */
@RunWith(AndroidJUnit4::class)
class LocalRepositoriesTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun clearDataStore() = runBlocking<Unit> {
        // The DataStore instance is process wide: start every test from an empty store.
        context.dataStore.edit { it.clear() }
        Locale.setDefault(Locale.US)
    }

    @Test
    fun `language defaults to the device language and persists`() = runBlocking<Unit> {
        val repository = LanguageRepository(context)
        assertThat(repository.get().first()).isEqualTo("en")
        assertThat(repository.save(SPANISH).isSuccess).isTrue()
        assertThat(repository.get().first()).isEqualTo("es")
        assertThat(LanguageHelper(repository).getCurrentLanguage()).isEqualTo("es")
    }

    @Test
    fun `seats arrangement defaults to DOWN and persists`() = runBlocking<Unit> {
        val repository = SelectedSeatsOrientationRepository(context)
        assertThat(repository.selectedSeatsArrangementFlow.first()).isEqualTo(SeatsArrangement.DOWN)
        SeatsArrangement.entries.forEach {
            repository.save(it)
            assertThat(repository.selectedSeatsArrangementFlow.first()).isEqualTo(it)
        }
    }

    @Test
    fun `diffs calculation feature defaults to enabled and persists`() = runBlocking<Unit> {
        val repository = DiffCalcsFeatureEnabledRepository(context)
        assertThat(repository.diffCalcsFeatureEnabledFlow.first()).isTrue()
        repository.save(false)
        assertThat(repository.diffCalcsFeatureEnabledFlow.first()).isFalse()
        repository.save(true)
        assertThat(repository.diffCalcsFeatureEnabledFlow.first()).isTrue()
    }

    @Test
    fun `set locale maps app languages and falls back to english`() {
        val expected = mapOf(
            CHINESE to "zh", DUTCH to "nl", ENGLISH to "en", FRENCH to "fr", SPANISH to "es", ITALIAN to "it", "de" to "en",
        )
        expected.forEach { (language, locale) ->
            val localized = context.setLocale(language)
            assertThat(localized.resources.configuration.locales[0].language).isEqualTo(locale)
            assertThat(Locale.getDefault().language).isEqualTo(locale)
        }
        Locale.setDefault(Locale.US)
    }

    /**
     * The full catalogue (81 MCR combinations) in every supported language, pinned in
     * golden/combinations/<language>.txt: points, localized name, image and localized description.
     */
    @Test
    fun `combinations catalogue in every language`() = runBlocking<Unit> {
        val languageRepository = LanguageRepository(context)
        listOf(ENGLISH, SPANISH, FRENCH, ITALIAN, DUTCH, CHINESE).forEach { language ->
            languageRepository.save(language)
            val combinations = CombinationsRepository(context, languageRepository).combinations.first()
            assertThat(combinations).hasLength(81)
            assertThat(combinations.map { it.combinationPoints }).isInOrder()
            assertThat(combinations.map { it.combinationPoints }.distinct())
                .containsExactly(1, 2, 4, 6, 8, 12, 16, 24, 32, 48, 64, 88).inOrder()
            combinations.forEach {
                // Every combination has either an image or a description.
                assertThat(it.combinationImage != null || it.combinationDescription != null).isTrue()
            }
            val text = combinations.joinToString("\n", postfix = "\n") { c ->
                val image = c.combinationImage?.let { id -> "@drawable/" + context.resources.getResourceEntryName(id) } ?: "-"
                "${c.combinationPoints} | ${c.combinationName} | $image | ${c.combinationDescription ?: "-"}"
            }
            Goldens.assertMatches("combinations/$language.txt", text)
            Locale.setDefault(Locale.US)
        }
    }

    @Test
    fun `strings exist in every supported language`() {
        val keys = listOf(R.string.app_name, R.string.old_games, R.string.combinations, R.string.game)
        listOf(ENGLISH, SPANISH, FRENCH, ITALIAN, DUTCH, CHINESE).forEach { language ->
            val localized = context.setLocale(language)
            keys.forEach { assertThat(localized.getString(it)).isNotEmpty() }
        }
        Locale.setDefault(Locale.US)
    }
}
