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
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllCombinationsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetFilteredCombinationsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundUseCase;

public class Injector extends BaseInjector {

    //REPOSITORIES
    private static GamesRepository provideGamesRepository(@NonNull Context context) { return new GamesRepository(context); }
    private static PlayersRepository providePlayersRepository(@NonNull Context context) { return new PlayersRepository(context); }
    private static CombinationsRepository provideCombinationsRepository(@NonNull Context context) { return new CombinationsRepository(context); }
    private static RoundsRepository provideRoundsRepository(@NonNull Context context) { return new RoundsRepository(context); }

    //USECASES
    private static CreateGameUseCase provideCreateGameUseCase(@NonNull Context context) {
        return new CreateGameUseCase(provideGamesRepository(context));
    }
    private static CreatePlayerUseCase provideCreatePlayerUseCase(@NonNull Context context) {
        return new CreatePlayerUseCase(providePlayersRepository(context));
    }
    private static DeleteGameUseCase provideDeleteGameUseCase(@NonNull Context context) {
        return new DeleteGameUseCase(provideGamesRepository(context));
    }
    private static GetAllCombinationsUseCase provideGetAllCombinationsUseCase(@NonNull Context context) {
        return new GetAllCombinationsUseCase(provideCombinationsRepository(context));
    }
    private static GetAllGamesUseCase provideGetAllGamesUseCase(@NonNull Context context) {
        return new GetAllGamesUseCase(provideGamesRepository(context));
    }
    private static GetAllPlayersUseCase provideGetAllPlayersUseCase(@NonNull Context context) {
        return new GetAllPlayersUseCase(providePlayersRepository(context));
    }
    private static GetFilteredCombinationsUseCase provideGetFilteredCombinationsUseCase(@NonNull Context context) {
        return new GetFilteredCombinationsUseCase(provideCombinationsRepository(context));
    }
//    public static GetGameUseCase provideGetGameUseCase(@NonNull Context context) {
//        return new GetGameUseCase(provideGamesRepository(context));
//    }
//    public static GetRoundsUseCase provideGetRoundsUseCase(@NonNull Context context) {
//        return new GetRoundsUseCase(provideRoundsRepository(context));
//    }
//    public static UpdateRoundUseCase provideUpdateRoundUseCase(@NonNull Context context) {
//        return new UpdateRoundUseCase(provideRoundsRepository(context));
//    }

    //VIEWMODELFACTORIES
    public static ViewModelProvider.Factory provideMainActivityViewModelFactory() {
        return new MainActivityViewModelFactory();
    }
    public static OldGamesViewModelFactory provideOldGamesViewModelFactory(@NonNull Context context) {
        return new OldGamesViewModelFactory(provideGetAllGamesUseCase(context), provideDeleteGameUseCase(context));
    }
    public static ViewModelProvider.Factory provideNewGameViewModelFactory(@NonNull Context context) {
        return new NewGameViewModelFactory(provideGetAllPlayersUseCase(context), provideCreatePlayerUseCase(context),
                provideCreateGameUseCase(context));
    }
    public static ViewModelProvider.Factory provideCombinationsViewModelFactory(@NonNull Context context) {
        return new CombinationsViewModelFactory(provideGetAllCombinationsUseCase(context), provideGetFilteredCombinationsUseCase(context));
    }
    public static ViewModelProvider.Factory provideGameActivityViewModelFactory(@NonNull Context context) {
        return new GameActivityViewModelFactory(provideGamesRepository(context), provideRoundsRepository(context));
    }
}
