package es.etologic.mahjongscoring2.app.game.game_list;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class GameListModule {

    @Provides
    GameListRvAdapter provideGameListRvAdapter(Context context) {
        return new GameListRvAdapter(context);
    }
}
