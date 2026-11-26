package com.etologic.mahjongscoring2.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces Dispatchers.Main (used by viewModelScope) with a real single "main" thread, like on a device.
 * An unconfined dispatcher would run view model code on whatever thread resumes it (Room, DataStore IO),
 * concurrently, which made state flows flaky.
 */
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(main)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

    private companion object {
        /**
         * One main thread for the whole JVM, never closed. DataStore is a process wide singleton that runs edit
         * transforms in the caller's context: closing the main thread of a previous test while one of its edits
         * was still queued left the next test's DataStore edit waiting forever.
         */
        val main: ExecutorCoroutineDispatcher by lazy { newSingleThreadContext("test-main") }
    }
}

/** Waits (max 20s, generous for slow CI machines) for the first value matching [predicate]. Work also happens on DataStore/IO threads. */
fun <T> Flow<T>.awaitFirst(predicate: (T) -> Boolean): T =
    runBlocking { withTimeout(20_000) { first(predicate) } }
