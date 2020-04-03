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
abstract class GameActivityFragmentsBuilder {
    
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
