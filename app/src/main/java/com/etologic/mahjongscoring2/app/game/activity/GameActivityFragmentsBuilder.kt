package com.etologic.mahjongscoring2.app.game.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.etologic.mahjongscoring2.app.game.dialogs.hand.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.players.PlayersDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.HuDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.ActionDialogFragment
import com.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment

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
    internal abstract fun providePlayersDialogFragment(): PlayersDialogFragment
    
    @ContributesAndroidInjector
    internal abstract fun provideRankingDialogFactory(): RankingDialogFragment
    
    @ContributesAndroidInjector
    internal abstract fun provideRollDiceDialogFragment(): RollDiceDialogFragment
}
