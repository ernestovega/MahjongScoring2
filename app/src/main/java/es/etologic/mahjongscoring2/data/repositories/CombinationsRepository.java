package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.CombinationsDao;
import es.etologic.mahjongscoring2.domain.entities.Combination;

import static es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase.NOT_SET_ID;

public class CombinationsRepository extends BaseRepository<Combination> { /*TODO Hacer pruebas forzando datos (tests unitarios!)*/

    private final CombinationsDao combinationsDao;
    private Context context;

    public CombinationsRepository(Context context) {
        super(context);
        this.context = context;
        combinationsDao = database.getCombinationsDao();
        if(getAll().isEmpty()) {
            combinationsDao.bulkInsert(hardcodedCombinations());
        }
    }
    @Override
    public List<Combination> getAll() {
        return combinationsDao.getAllOrderedAscByPoints();
    }
    private List<Combination> hardcodedCombinations() {
        List<Combination> combinations = new ArrayList<>();
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.pure_double_chow), R.drawable.pure_double_chow));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.mixed_double_chow), R.drawable.mixed_double_chow));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.short_straight), R.drawable.short_straight));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.two_terminal_chows), R.drawable.two_terminal_chows));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.pung_of_terminals_or_honors), R.drawable.pung_of_terminal_or_honors));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.melded_kong), R.drawable.melded_kong));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.one_voided_suit), R.drawable.one_voided_suit));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.no_honors), R.drawable.no_honors));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.edge_wait), context.getString(R.string.description_edge_wait)));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.closed_wait), context.getString(R.string.description_closed_wait)));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.single_waiting), context.getString(R.string.description_single_waiting)));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.self_drawn), context.getString(R.string.description_self_drawn)));
        combinations.add(new Combination(1, NOT_SET_ID, context.getString(R.string.flower_tiles), R.drawable.flower_tiles));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.dragon_pung), R.drawable.dragon_pung));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.prevalent_wind), R.drawable.prevalent_wind));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.seat_wind), R.drawable.seat_wind));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.concealed_hand), context.getString(R.string.description_concealed_hand)));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.all_chows), R.drawable.all_chows));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.tile_hog), R.drawable.tile_hog));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.double_pungs), R.drawable.double_pungs));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.two_concealed_pungs), R.drawable.two_concealed_pungs));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.concealed_kong), R.drawable.concealed_kong));
        combinations.add(new Combination(2, NOT_SET_ID, context.getString(R.string.all_simples), R.drawable.all_simples));
        combinations.add(new Combination(4, NOT_SET_ID, context.getString(R.string.outside_hand), R.drawable.outside_hand));
        combinations.add(new Combination(4, NOT_SET_ID, context.getString(R.string.fully_concealed_hand), context.getString(R.string.description_fully_concealed_hand)));
        combinations.add(new Combination(4, NOT_SET_ID, context.getString(R.string.two_melded_kongs), R.drawable.two_melded_kongs));
        combinations.add(new Combination(4, NOT_SET_ID, context.getString(R.string.last_tile), context.getString(R.string.description_last_tile)));
        combinations.add(new Combination(6, NOT_SET_ID, context.getString(R.string.all_pungs), R.drawable.all_pungs));
        combinations.add(new Combination(6, NOT_SET_ID, context.getString(R.string.half_flush), R.drawable.half_flush));
        combinations.add(new Combination(6, NOT_SET_ID, context.getString(R.string.mixed_shifted_chows), R.drawable.mixed_shifted_chows));
        combinations.add(new Combination(6, NOT_SET_ID, context.getString(R.string.all_types), R.drawable.all_types));
        combinations.add(new Combination(6, NOT_SET_ID, context.getString(R.string.melded_hand), context.getString(R.string.description_melded_hand)));
        combinations.add(new Combination(6, NOT_SET_ID, context.getString(R.string.two_dragon_pungs), R.drawable.two_dragon_pungs));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.mixed_straight), R.drawable.mixed_straight));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.reversible_tiles), R.drawable.reversible_tiles));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.mixed_triple_chow), R.drawable.mixed_triple_chow));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.mixed_shifted_pungs), R.drawable.mixed_shifted_pungs));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.chicken_hand), R.drawable.chicken_hand));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.last_tile_draw), context.getString(R.string.description_last_tile_draw)));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.last_tile_claim), context.getString(R.string.description_last_tile_claim)));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.out_with_replacement_tile), context.getString(R.string.description_out_with_replacement_tile)));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.robbing_the_kong), context.getString(R.string.description_robbing_the_kong)));
        combinations.add(new Combination(8, NOT_SET_ID, context.getString(R.string.two_concealed_kongs), R.drawable.two_concealed_kongs));
        combinations.add(new Combination(12, NOT_SET_ID, context.getString(R.string.lesser_honors_and_knitted_tiles), R.drawable.lesser_honors_and_knitted_tiles));
        combinations.add(new Combination(12, NOT_SET_ID, context.getString(R.string.knitted_straight), R.drawable.knitted_straight));
        combinations.add(new Combination(12, NOT_SET_ID, context.getString(R.string.upper_four), R.drawable.upper_four));
        combinations.add(new Combination(12, NOT_SET_ID, context.getString(R.string.lower_four), R.drawable.lower_four));
        combinations.add(new Combination(12, NOT_SET_ID, context.getString(R.string.big_three_winds), R.drawable.big_three_winds));
        combinations.add(new Combination(16, NOT_SET_ID, context.getString(R.string.pure_straight), R.drawable.pure_straight));
        combinations.add(new Combination(16, NOT_SET_ID, context.getString(R.string.three_suited_terminal_chows), R.drawable.three_suited_terminal_chows));
        combinations.add(new Combination(16, NOT_SET_ID, context.getString(R.string.pure_shifted_chows), R.drawable.pure_shifted_chows));
        combinations.add(new Combination(16, NOT_SET_ID, context.getString(R.string.all_fives), R.drawable.all_fives));
        combinations.add(new Combination(16, NOT_SET_ID, context.getString(R.string.triple_pungs), R.drawable.triple_pungs));
        combinations.add(new Combination(16, NOT_SET_ID, context.getString(R.string.three_concealed_pungs), R.drawable.three_concealed_pungs));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.seven_pairs), R.drawable.seven_pairs));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.greater_honors_and_knitted_tiles), R.drawable.greater_honors_and_knitted_tiles));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.all_even_pungs), R.drawable.all_even_pungs));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.full_flush), R.drawable.full_flush));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.pure_triple_chow), R.drawable.pure_triple_chow));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.pure_shifted_pungs), R.drawable.pure_shifted_pungs));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.upper_tiles), R.drawable.upper_tiles));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.medium_tiles), R.drawable.medium_tiles));
        combinations.add(new Combination(24, NOT_SET_ID, context.getString(R.string.lower_tiles), R.drawable.lower_tiles));
        combinations.add(new Combination(32, NOT_SET_ID, context.getString(R.string.four_pure_shifted_chows), R.drawable.four_pure_shifted_chows));
        combinations.add(new Combination(32, NOT_SET_ID, context.getString(R.string.three_kongs), R.drawable.three_kongs));
        combinations.add(new Combination(32, NOT_SET_ID, context.getString(R.string.all_terminals_and_honors), R.drawable.all_terminals_and_honors));
        combinations.add(new Combination(48, NOT_SET_ID, context.getString(R.string.quadruple_chow), R.drawable.quadruple_chow));
        combinations.add(new Combination(48, NOT_SET_ID, context.getString(R.string.four_pure_shifted_pungs), R.drawable.four_pure_shifted_pungs));
        combinations.add(new Combination(64, NOT_SET_ID, context.getString(R.string.all_terminals), R.drawable.all_terminals));
        combinations.add(new Combination(64, NOT_SET_ID, context.getString(R.string.all_honors), R.drawable.all_honors));
        combinations.add(new Combination(64, NOT_SET_ID, context.getString(R.string.little_four_winds), R.drawable.little_four_winds));
        combinations.add(new Combination(64, NOT_SET_ID, context.getString(R.string.little_three_dragons), R.drawable.little_three_dragons));
        combinations.add(new Combination(64, NOT_SET_ID, context.getString(R.string.four_concealed_pungs), R.drawable.four_concealed_pungs));
        combinations.add(new Combination(64, NOT_SET_ID, context.getString(R.string.pure_terminal_chows), R.drawable.pure_terminal_chows));
        combinations.add(new Combination(88, NOT_SET_ID, context.getString(R.string.big_four_winds), R.drawable.big_four_winds));
        combinations.add(new Combination(88, NOT_SET_ID, context.getString(R.string.big_three_dragons), R.drawable.big_three_dragons));
        combinations.add(new Combination(88, NOT_SET_ID, context.getString(R.string.all_green), R.drawable.all_green));
        combinations.add(new Combination(88, NOT_SET_ID, context.getString(R.string.nine_gates), R.drawable.nine_gates));
        combinations.add(new Combination(88, NOT_SET_ID, context.getString(R.string.four_kongs), R.drawable.four_kongs));
        combinations.add(new Combination(88, NOT_SET_ID, context.getString(R.string.seven_shifted_pairs), R.drawable.seven_shifted_pairs));
        return combinations;
    }
    @Override
    public long insertOne(Combination combination) {
        try {
            return combinationsDao.insertOne(combination);
        } catch(SQLiteConstraintException exception) {
            return 0;
        }
    }
    @Override
    public Combination getOne(long combinationId) {
        Combination combination = combinationsDao.getOne(combinationId);
        if(combination == null) { //TODO: Ver si devuelve null?
            combinationsDao.bulkInsert(hardcodedCombinations());
            combination = combinationsDao.getOne(combinationId);
        }
        return combination;
    }
    public List<Combination> getFilteredCombinations(String filter) {
        return combinationsDao.getFilteredCombinations(String.format("%%%s%%", filter));
    }

    @Override
    public boolean updateOne(Combination combination) {
        return combinationsDao.updateOne(combination) == 1;
    }

    @Override
    public boolean deleteOne(long combinationId) {
        return combinationsDao.deleteOne(combinationId) == 1;
    }

    @Override
    public long deleteAll() {
        return combinationsDao.deleteAll();
    }
}
