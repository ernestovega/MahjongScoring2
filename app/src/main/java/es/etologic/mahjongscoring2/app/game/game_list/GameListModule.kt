package es.etologic.mahjongscoring2.app.game.game_list

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class GameListModule {
    
    @Provides
    internal fun provideGameListRvAdapter(context: Context): GameListRvAdapter {
        return GameListRvAdapter(context)
    }
}
