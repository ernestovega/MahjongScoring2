package es.etologic.mahjongscoring2.app.game.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import es.etologic.mahjongscoring2.app.game.dialogs.RankingDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.PlayersDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.PenaltyDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.PointsDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.RollDiceDialogFragment
import es.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import es.etologic.mahjongscoring2.app.game.game_list.GameListModule
import es.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import es.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment

@Module
abstract class GameActivityFragmentsBuilder {
    
    @ContributesAndroidInjector(modules = [GameListModule::class])
    internal abstract fun provideGameListFragmentFactory(): GameListFragment
    
    @ContributesAndroidInjector
    internal abstract fun provideGameTableFragmentFactory(): GameTableFragment
    
    @ContributesAndroidInjector
    internal abstract fun provideGameTableSeatFragmentFactory(): GameTableSeatsFragment
    
    @ContributesAndroidInjector
    internal abstract fun provideGameTableRankingFragmentDialogFactory(): RankingDialogFragment
    
    @ContributesAndroidInjector
    internal abstract fun provideRollDiceDialogFragment(): RollDiceDialogFragment
    
    @ContributesAndroidInjector
    internal abstract fun providePlayersDialogFragment(): PlayersDialogFragment
    
    @ContributesAndroidInjector
    internal abstract fun providePointsDialogFragment(): PointsDialogFragment
    
    @ContributesAndroidInjector
    internal abstract fun providePenaltyDialogFragment(): PenaltyDialogFragment
}
