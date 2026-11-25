package com.etologic.mahjongscoring2.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/** Replaces Dispatchers.Main (used by viewModelScope) with an unconfined test dispatcher. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(UnconfinedTestDispatcher())
    override fun finished(description: Description) = Dispatchers.resetMain()
}

/** Waits (max 5s) for the first value matching [predicate]. Work also happens on DataStore/IO threads. */
fun <T> Flow<T>.awaitFirst(predicate: (T) -> Boolean): T =
    runBlocking { withTimeout(5_000) { first(predicate) } }
