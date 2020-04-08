/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.game.activity

import com.etologic.mahjongscoring2.business.use_cases.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SaveCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.GameActionsUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.PenaltyUseCase
import dagger.Module
import dagger.Provides

@Module
class GameActivityModule {
    
    @Provides
    internal fun provideGameViewModelFactory(
        getCurrentGameUseCase: GetCurrentGameUseCase,
        saveCurrentPlayersUseCase: SaveCurrentPlayersUseCase,
        gameActionsUseCase: GameActionsUseCase,
        penaltyUseCase: PenaltyUseCase
    ): GameActivityViewModelFactory {
        return GameActivityViewModelFactory(
            getCurrentGameUseCase,
            saveCurrentPlayersUseCase,
            gameActionsUseCase,
            penaltyUseCase
        )
    }
}
