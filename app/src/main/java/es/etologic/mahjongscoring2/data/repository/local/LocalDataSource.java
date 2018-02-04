package es.etologic.mahjongscoring2.data.repository.local;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.data.repository.local.daos.GamesDao;
import es.etologic.mahjongscoring2.data.repository.local.daos.PlayersDao;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public class LocalDataSource implements ILocalDataSource {

    //region Fields

    private final Context context;
    private final PlayersDao playersDao;
    private final GamesDao gamesDao;

    //endregion

    //region Constructor

    public LocalDataSource(Context context) {
        this.context = context;
        AppDatabase database = AppDatabase.getInstance(this.context.getApplicationContext());
        playersDao = database.getPlayersDao();
        gamesDao = database.getGamesDao();
    }

    //endregion

    //region DB

    @Override
    public void clearDatabase() {}

    //endregion

    //region GAMES

    @Override
    public long insertGame(Game game) {
        try {
            return gamesDao.insertOne(game);
        } catch(SQLiteConstraintException exception) {
            return 0;
            //TODO Hacer pruebas forzando datos (tests unitarios!)
        }
    }

    @Override
    public Game getGame(long gameId) {
        return gamesDao.getOne(gameId);
        //TODO Hacer pruebas forzando datos (tests unitarios!)
    }

    @Override
    public List<Game> getAllGames() {
        return gamesDao.getAll();
    }

    @Override
    public boolean deleteGame(long gameId) {
        return gamesDao.deleteOne(gameId) == 1;
    }

    //endregion

    //region PLAYERS

    @Override
    public boolean insertPlayer(Player player) {
        try {
            playersDao.insert(player);
        } catch(SQLiteConstraintException exception) {
            return false;
            //TODO Hacer pruebas forzando datos (tests unitarios!)
        }
        return true;
    }

    @Override
    public Player getPlayer(String playerName) {
        return playersDao.getOne(playerName);
        //TODO Hacer pruebas forzando datos (tests unitarios!)
    }

    @Override
    public List<Player> getAllPlayers() {
        return playersDao.getAll();
    }

    //endregion

    //region COMBINATIONS

    @Override
    public List<Combination> getAllCombinations() {
        List<Combination> combinations = new ArrayList<>();
        combinations.add(new Combination(1, R.string.pure_double_chow, R.drawable.pure_double_chow, -1));
        combinations.add(new Combination(1, R.string.mixed_double_chow, R.drawable.mixed_double_chow, -1));
        combinations.add(new Combination(1, R.string.short_straight, R.drawable.short_straight, -1));
        combinations.add(new Combination(1, R.string.two_terminal_chows, R.drawable.two_terminal_chows, -1));
        combinations.add(new Combination(1, R.string.pung_of_terminals_or_honors, R.drawable.pung_of_terminal_or_honors, -1));
        combinations.add(new Combination(1, R.string.melded_kong, R.drawable.melded_kong, -1));
        combinations.add(new Combination(1, R.string.one_voided_suit, R.drawable.one_voided_suit, -1));
        combinations.add(new Combination(1, R.string.no_honors, R.drawable.no_honors, -1));
        combinations.add(new Combination(1, R.string.edge_wait, -1, R.string.description_edge_wait));
        combinations.add(new Combination(1, R.string.closed_wait, -1, R.string.description_closed_wait));
        combinations.add(new Combination(1, R.string.single_waiting, -1, R.string.description_single_waiting));
        combinations.add(new Combination(1, R.string.self_drawn, -1, R.string.description_self_drawn));
        combinations.add(new Combination(1, R.string.flower_tiles, R.drawable.flower_tiles, -1));

        combinations.add(new Combination(2, R.string.dragon_pung, R.drawable.dragon_pung, -1));
        combinations.add(new Combination(2, R.string.prevalent_wind, R.drawable.prevalent_wind, -1));
        combinations.add(new Combination(2, R.string.seat_wind, R.drawable.seat_wind, -1));
        combinations.add(new Combination(2, R.string.concealed_hand, -1, R.string.description_concealed_hand));
        combinations.add(new Combination(2, R.string.all_chows, R.drawable.all_chows, -1));
        combinations.add(new Combination(2, R.string.tile_hog, R.drawable.tile_hog, -1));
        combinations.add(new Combination(2, R.string.double_pungs, R.drawable.double_pungs, -1));
        combinations.add(new Combination(2, R.string.two_concealed_pungs, R.drawable.two_concealed_pungs, -1));
        combinations.add(new Combination(2, R.string.concealed_kong, R.drawable.concealed_kong, -1));
        combinations.add(new Combination(2, R.string.all_simples, R.drawable.all_simples, -1));

        combinations.add(new Combination(4, R.string.outside_hand, R.drawable.outside_hand, -1));
        combinations.add(new Combination(4, R.string.fully_concealed_hand, -1, R.string.description_fully_concealed_hand));
        combinations.add(new Combination(4, R.string.two_melded_kongs, R.drawable.two_melded_kongs, -1));
        combinations.add(new Combination(4, R.string.last_tile, -1, R.string.description_last_tile));

        combinations.add(new Combination(6, R.string.all_pungs, R.drawable.all_pungs, -1));
        combinations.add(new Combination(6, R.string.half_flush, R.drawable.half_flush, -1));
        combinations.add(new Combination(6, R.string.mixed_shifted_chows, R.drawable.mixed_shifted_chows, -1));
        combinations.add(new Combination(6, R.string.all_types, R.drawable.all_types, -1));
        combinations.add(new Combination(6, R.string.melded_hand, -1, R.string.description_melded_hand));
        combinations.add(new Combination(6, R.string.two_dragon_pungs, R.drawable.two_dragon_pungs, -1));

        combinations.add(new Combination(8, R.string.mixed_straight, R.drawable.mixed_straight, -1));
        combinations.add(new Combination(8, R.string.reversible_tiles, R.drawable.reversible_tiles, -1));
        combinations.add(new Combination(8, R.string.mixed_triple_chow, R.drawable.mixed_triple_chow, -1));
        combinations.add(new Combination(8, R.string.mixed_shifted_pungs, R.drawable.mixed_shifted_pungs, -1));
        combinations.add(new Combination(8, R.string.chicken_hand, R.drawable.chicken_hand, -1));
        combinations.add(new Combination(8, R.string.last_tile_draw, -1, R.string.description_last_tile_draw));
        combinations.add(new Combination(8, R.string.last_tile_claim, -1, R.string.description_last_tile_claim));
        combinations.add(new Combination(8, R.string.out_with_replacement_tile, -1, R.string.description_out_with_replacement_tile));
        combinations.add(new Combination(8, R.string.robbing_the_kong, -1, R.string.description_robbing_the_kong));
        combinations.add(new Combination(8, R.string.two_concealed_kongs, R.drawable.two_concealed_kongs, -1));

        combinations.add(new Combination(12, R.string.lesser_honors_and_knitted_tiles, R.drawable.lesser_honors_and_knitted_tiles, -1));
        combinations.add(new Combination(12, R.string.knitted_straight, R.drawable.knitted_straight, -1));
        combinations.add(new Combination(12, R.string.upper_four, R.drawable.upper_four, -1));
        combinations.add(new Combination(12, R.string.lower_four, R.drawable.lower_four, -1));
        combinations.add(new Combination(12, R.string.big_three_winds, R.drawable.big_three_winds, -1));

        combinations.add(new Combination(16, R.string.pure_straight, R.drawable.pure_straight, -1));
        combinations.add(new Combination(16, R.string.three_suited_terminal_chows, R.drawable.three_suited_terminal_chows, -1));
        combinations.add(new Combination(16, R.string.pure_shifted_chows, R.drawable.pure_shifted_chows, -1));
        combinations.add(new Combination(16, R.string.all_fives, R.drawable.all_fives, -1));
        combinations.add(new Combination(16, R.string.triple_pungs, R.drawable.triple_pungs, -1));
        combinations.add(new Combination(16, R.string.three_concealed_pungs, R.drawable.three_concealed_pungs, -1));

        combinations.add(new Combination(24, R.string.seven_pairs, R.drawable.seven_pairs, -1));
        combinations.add(new Combination(24, R.string.greater_honors_and_knitted_tiles, R.drawable.greater_honors_and_knitted_tiles, -1));
        combinations.add(new Combination(24, R.string.all_even_pungs, R.drawable.all_even_pungs, -1));
        combinations.add(new Combination(24, R.string.full_flush, R.drawable.full_flush, -1));
        combinations.add(new Combination(24, R.string.pure_triple_chow, R.drawable.pure_triple_chow, -1));
        combinations.add(new Combination(24, R.string.pure_shifted_pungs, R.drawable.pure_shifted_pungs, -1));
        combinations.add(new Combination(24, R.string.upper_tiles, R.drawable.upper_tiles, -1));
        combinations.add(new Combination(24, R.string.medium_tiles, R.drawable.medium_tiles, -1));
        combinations.add(new Combination(24, R.string.lower_tiles, R.drawable.lower_tiles, -1));

        combinations.add(new Combination(32, R.string.four_pure_shifted_chows, R.drawable.four_pure_shifted_chows, -1));
        combinations.add(new Combination(32, R.string.three_kongs, R.drawable.three_kongs, -1));
        combinations.add(new Combination(32, R.string.all_terminals_and_honors, R.drawable.all_terminals_and_honors, -1));

        combinations.add(new Combination(48, R.string.quadruple_chow, R.drawable.quadruple_chow, -1));
        combinations.add(new Combination(48, R.string.four_pure_shifted_pungs, R.drawable.four_pure_shifted_pungs, -1));

        combinations.add(new Combination(64, R.string.all_terminals, R.drawable.all_terminals, -1));
        combinations.add(new Combination(64, R.string.all_honors, R.drawable.all_honors, -1));
        combinations.add(new Combination(64, R.string.little_four_winds, R.drawable.little_four_winds, -1));
        combinations.add(new Combination(64, R.string.little_three_dragons, R.drawable.little_three_dragons, -1));
        combinations.add(new Combination(64, R.string.four_concealed_pungs, R.drawable.four_concealed_pungs, -1));
        combinations.add(new Combination(64, R.string.pure_terminal_chows, R.drawable.pure_terminal_chows, -1));

        combinations.add(new Combination(88, R.string.big_four_winds, R.drawable.big_four_winds, -1));
        combinations.add(new Combination(88, R.string.big_three_dragons, R.drawable.big_three_dragons, -1));
        combinations.add(new Combination(88, R.string.all_green, R.drawable.all_green, -1));
        combinations.add(new Combination(88, R.string.nine_gates, R.drawable.nine_gates, -1));
        combinations.add(new Combination(88, R.string.four_kongs, R.drawable.four_kongs, -1));
        combinations.add(new Combination(88, R.string.seven_shifted_pairs, R.drawable.seven_shifted_pairs, -1));
        return combinations;
    }
}