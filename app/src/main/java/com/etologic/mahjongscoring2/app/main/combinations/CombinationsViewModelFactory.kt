package com.etologic.mahjongscoring2.app.main.combinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.business.use_cases.combinations.GetCombinationsUseCase
import javax.inject.Inject

class CombinationsViewModelFactory @Inject
constructor(private val getCombinationsUseCase: GetCombinationsUseCase) : ViewModelProvider.NewInstanceFactory() {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CombinationsViewModel(getCombinationsUseCase) as T
    }
}
