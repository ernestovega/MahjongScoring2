package es.etologic.mahjongscoring2;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.main.combinations.CombinationsViewModelFactory;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.main.new_game.NewGameViewModelFactory;
import es.etologic.mahjongscoring2.app.main.old_games.OldGamesViewModelFactory;
import es.etologic.mahjongscoring2.data.repositories.CombinationsRepository;
import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository;

public class Injector extends BaseInjector {

    //VIEWMODELFACTORIES
    public static ViewModelProvider.Factory provideMainActivityViewModelFactory() {
        return new MainActivityViewModelFactory();
    }

    public static OldGamesViewModelFactory provideOldGamesViewModelFactory(@NonNull Context context) {
        return new OldGamesViewModelFactory(provideGamesRepository(context));
    }

    public static ViewModelProvider.Factory provideNewGameViewModelFactory(Context context) {
        return new NewGameViewModelFactory(providePlayersRepository(context), provideGamesRepository(context));
    }

    public static ViewModelProvider.Factory provideCombinationsViewModelFactory(Context context) {
        return new CombinationsViewModelFactory(provideCombinationsRepository(context));
    }

    public static ViewModelProvider.Factory provideGameActivityViewModelFactory(@NonNull Context context) {
        return new GameActivityViewModelFactory(provideGamesRepository(context), provideRoundsRepository(context));
    }

    //REPOSITORIES
    private static GamesRepository provideGamesRepository(@NonNull Context context) {
        return new GamesRepository(context);
    }

    private static PlayersRepository providePlayersRepository(Context context) {
        return new PlayersRepository(context);
    }

    private static CombinationsRepository provideCombinationsRepository(Context context) {
        return new CombinationsRepository(context);
    }

    private static RoundsRepository provideRoundsRepository(@NonNull Context context) {
        return new RoundsRepository(context);
    }
}
