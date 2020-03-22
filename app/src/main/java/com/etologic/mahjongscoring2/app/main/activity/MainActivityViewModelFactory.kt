package com.etologic.mahjongscoring2.app.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.business.use_cases.games.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.current_game.SetCurrentGameUseCase

class MainActivityViewModelFactory(
    private val createGameUseCase: CreateGameUseCase,
    private val setCurrentGameUseCase: SetCurrentGameUseCase
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(
            createGameUseCase,
            setCurrentGameUseCase
        ) as T
    }
}
