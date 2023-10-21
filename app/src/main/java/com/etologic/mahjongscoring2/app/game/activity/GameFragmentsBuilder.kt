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

import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.ActionDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.DiscarderDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.HuDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.names.NamesDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment
import com.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class GameFragmentsBuilder {

    @ContributesAndroidInjector
    internal abstract fun provideGameListFragmentFactory(): GameListFragment

    @ContributesAndroidInjector
    internal abstract fun provideGameTableFragmentFactory(): GameTableFragment

    @ContributesAndroidInjector
    internal abstract fun provideGameTableSeatFragmentFactory(): GameTableSeatsFragment

    @ContributesAndroidInjector
    internal abstract fun provideHandActionsFragment(): ActionDialogFragment

    @ContributesAndroidInjector
    internal abstract fun providePointsFragment(): HuDialogFragment

    @ContributesAndroidInjector
    internal abstract fun providePenaltyFragment(): PenaltyDialogFragment

    @ContributesAndroidInjector
    internal abstract fun providePlayersDialogFragment(): NamesDialogFragment

    @ContributesAndroidInjector
    internal abstract fun provideRankingDialogFactory(): RankingDialogFragment

    @ContributesAndroidInjector
    internal abstract fun provideRollDiceDialogFragment(): RollDiceDialogFragment

    @ContributesAndroidInjector
    internal abstract fun provideDiscarderConfirmDialogFragment(): DiscarderDialogFragment
}
