package com.etologic.mahjongscoring2.app.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.business.use_cases.current_game.SetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.CreateGameUseCase

class MainActivityViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainActivityViewModel() as T
}
