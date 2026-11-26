package com.etologic.mahjongscoring2.ui

import android.content.Context
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.viewpager2.widget.ViewPager2
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import com.etologic.mahjongscoring2.business.use_cases.mappers.toUiGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import kotlinx.coroutines.flow.first
import org.hamcrest.Matchers.allOf
import org.robolectric.shadows.ShadowAlertDialog
import org.robolectric.shadows.ShadowDialog
import android.os.Looper
import android.view.View
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.core.view.GravityCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.app.screens.MainViewModel
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.data_source.local_data_sources.datastore.dataStore
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.repositories.DiffCalcsFeatureEnabledRepository
import com.etologic.mahjongscoring2.data_source.repositories.LanguageRepository
import com.etologic.mahjongscoring2.data_source.repositories.SelectedSeatsOrientationRepository
import com.etologic.mahjongscoring2.testutil.GameScenario
import com.etologic.mahjongscoring2.testutil.clone
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureScreenRoboImage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper
import java.io.File
import java.time.Duration
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * Base class for the UI tests: real MainActivity, real fragments, real Hilt graph, with an in-memory
 * Room database ([TestDatabaseModule]) seeded from [GameScenario]s. Runs on the JVM with Robolectric
 * native graphics, so the same tests produce the golden screenshots (Roborazzi).
 *
 * Device: 411x891dp xxhdpi (Pixel 7 class), portrait, as the app is locked to portrait.
 */
@Config(application = HiltTestApplication::class, qualifiers = UiTestBase.DEVICE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class UiTestBase {

    companion object {
        const val DEVICE = "w411dp-h891dp-normal-long-notround-any-420dpi-keyshidden-nonav"
        private const val SETTLE_STEP_MS = 50L
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var languageRepository: LanguageRepository

    protected val context: Context get() = ApplicationProvider.getApplicationContext()

    protected var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun baseSetUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Locale.setDefault(Locale.US)
        hiltRule.inject()
        clearFileProviderCache()
        runBlocking {
            context.dataStore.edit { it.clear() }
            languageRepository.save("en")
        }
    }

    /**
     * androidx FileProvider caches its roots in a static map, but every Robolectric test gets a new temporary
     * files directory: without this, only the first test that shares a file would find its root.
     */
    private fun clearFileProviderCache() {
        runCatching {
            val field = androidx.core.content.FileProvider::class.java.getDeclaredField("sCache").apply { isAccessible = true }
            (field.get(null) as MutableMap<*, *>).clear()
        }
    }

    @After
    fun baseTearDown() {
        IdlingRegistry.getInstance().resources.forEach { IdlingRegistry.getInstance().unregister(it) }
        scenario?.close()
        Locale.setDefault(Locale.US)
    }

    // region Data

    protected fun seed(vararg scenarios: GameScenario) = runBlocking {
        scenarios.forEach { s ->
            database.gamesDao.insertOne(s.game)
            s.rounds.forEach { database.roundsDao.insertOne(it.clone()) }
        }
    }

    protected fun setSeatsArrangement(arrangement: SeatsArrangement) = runBlocking {
        SelectedSeatsOrientationRepository(context).save(arrangement)
    }

    protected fun setDiffsFeatureEnabled(enabled: Boolean) = runBlocking {
        DiffCalcsFeatureEnabledRepository(context).save(enabled)
    }

    // endregion

    // region Navigation

    protected fun launchApp(): ActivityScenario<MainActivity> =
        ActivityScenario.launch(MainActivity::class.java).also {
            scenario = it
            settle()
        }

    protected fun openGame(gameId: GameId) {
        scenario!!.onActivity { activity ->
            ViewModelProvider(activity)[MainViewModel::class.java].updateActiveGameId(gameId)
            activity.findNavController(R.id.fragmentContainerView).navigate(R.id.gameFragment)
        }
        settle()
    }

    protected fun navigateTo(destinationId: Int) {
        scenario!!.onActivity { it.findNavController(R.id.fragmentContainerView).navigate(destinationId) }
        settle()
    }

    protected fun currentDestination(): Int? {
        var id: Int? = null
        scenario!!.onActivity { id = it.findNavController(R.id.fragmentContainerView).currentDestination?.id }
        return id
    }

    /**
     * Runs pending main looper work, coroutine delays and animations, and lets IO threads finish.
     * The fake clock advances in small steps, each giving background threads (Room, DataStore) a little real
     * time, so app code that races a delay() against a database write behaves as it does on a device.
     */
    protected fun settle(seconds: Long = 2) {
        repeat((seconds * 1000 / SETTLE_STEP_MS).toInt()) {
            Thread.sleep(10)
            ShadowLooper.idleMainLooper()
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(SETTLE_STEP_MS))
        }
        Thread.sleep(20)
        ShadowLooper.idleMainLooper()
    }

    /**
     * Clicks a view by calling performClick() directly (no touch hit-testing, so overlapping views do not
     * steal the click), after advancing the clock past the app's 600ms anti double click guard.
     */
    protected fun ViewInteraction.safeClick(): ViewInteraction {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1))
        return perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> = isEnabled()
            override fun getDescription() = "performClick()"
            override fun perform(uiController: UiController, view: View) {
                view.performClick()
                uiController.loopMainThreadUntilIdle()
            }
        }).also { settle() }
    }

    /** Sends only an ACTION_DOWN to the view: the finger stays pressed (used by "hold to show diffs"). */
    protected fun ViewInteraction.pressAndHold(): ViewInteraction = perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> = isEnabled()
        override fun getDescription() = "ACTION_DOWN"
        override fun perform(uiController: UiController, view: View) {
            val now = android.os.SystemClock.uptimeMillis()
            view.dispatchTouchEvent(android.view.MotionEvent.obtain(now, now, android.view.MotionEvent.ACTION_DOWN, view.width / 2f, view.height / 2f, 0))
            uiController.loopMainThreadUntilIdle()
        }
    }).also { settle() }

    /** Sends ACTION_UP to release a [pressAndHold]. */
    protected fun ViewInteraction.release(): ViewInteraction = perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> = isEnabled()
        override fun getDescription() = "ACTION_UP"
        override fun perform(uiController: UiController, view: View) {
            val now = android.os.SystemClock.uptimeMillis()
            view.dispatchTouchEvent(android.view.MotionEvent.obtain(now, now, android.view.MotionEvent.ACTION_UP, view.width / 2f, view.height / 2f, 0))
            uiController.loopMainThreadUntilIdle()
        }
    }).also { settle() }

    /** Views inside a dialog window (Espresso otherwise only looks at the activity window). */
    protected fun onDialogView(matcher: Matcher<View>): ViewInteraction = onView(matcher).inRoot(isDialog())

    protected fun onDialogView(id: Int): ViewInteraction = onDialogView(withId(id))

    protected fun openDrawer() {
        scenario!!.onActivity { it.binding.drawerLayout.openDrawer(GravityCompat.START, false) }
        settle()
    }

    protected fun waitFor(seconds: Long): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isRoot()
        override fun getDescription() = "wait $seconds s"
        override fun perform(uiController: UiController, view: View) = uiController.loopMainThreadForAtLeast(seconds * 1000)
    }

    // endregion

    // region Common user actions and queries

    protected fun rounds(gameId: GameId): List<DbRound> = runBlocking { database.roundsDao.getGameRounds(gameId) }

    protected fun game(gameId: GameId) = runBlocking { database.gamesDao.getOne(gameId).toUiGame(rounds(gameId)) }

    protected fun allGames() = runBlocking { database.gamesDao.getAllFlow().first() }

    protected fun seat(seatId: Int) = onView(allOf(withId(R.id.tvTableSeatBySeatName), isDescendantOfA(withId(seatId))))

    protected fun seatPoints(seatId: Int) = onView(allOf(withId(R.id.tvTableSeatBySeatPoints), isDescendantOfA(withId(seatId))))

    protected fun typePoints(numPadId: Int, points: Int) {
        val digitIds = listOf(
            R.id.btCustomNumPad0, R.id.btCustomNumPad1, R.id.btCustomNumPad2, R.id.btCustomNumPad3, R.id.btCustomNumPad4,
            R.id.btCustomNumPad5, R.id.btCustomNumPad6, R.id.btCustomNumPad7, R.id.btCustomNumPad8, R.id.btCustomNumPad9,
        )
        points.toString().forEach { digit ->
            onDialogView(allOf(withId(digitIds[digit.digitToInt()]), isDescendantOfA(withId(numPadId)))).safeClick()
        }
    }

    protected fun gamePagerItem(): Int {
        var item = -1
        scenario!!.onActivity { item = it.findViewById<ViewPager2>(R.id.viewPagerGame).currentItem }
        return item
    }

    protected fun clickMenu(itemId: Int) {
        scenario!!.onActivity { shadowOf(it).clickMenuItem(itemId) }
        settle()
    }

    protected fun confirmLatestAlertDialog() {
        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
        settle()
    }

    protected fun performClickWithoutAdvancingTime() = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isEnabled()
        override fun getDescription() = "performClick() without advancing the clock"
        override fun perform(uiController: UiController, view: View) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1)) // past the anti double click guard
            view.performClick()
        }
    }

    protected fun isDialogShowing(): Boolean = ShadowDialog.getLatestDialog()?.isShowing == true

    // endregion

    // region Screenshots

    private val screenshotsDir: File
        get() = File(System.getProperty("screenshots.dir") ?: error("screenshots.dir not set"))

    /** Captures the whole screen, including dialogs, into src/test/screenshots/<name>.png. */
    @OptIn(ExperimentalRoborazziApi::class)
    protected fun screenshot(name: String) {
        settle()
        onView(isRoot()).check { _, _ -> }
        captureScreenRoboImage(
            file = File(screenshotsDir, "$name.png"),
            roborazziOptions = RoborazziOptions(
                recordOptions = RoborazziOptions.RecordOptions(resizeScale = 0.5),
            ),
        )
    }

    // endregion
}
