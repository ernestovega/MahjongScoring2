package es.etologic.mahjongscoring2.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

public class RankingTableMapper {
    private static final String FIRST_POSITION_POINTS = "4";
    private static final String SECOND_POSITION_POINTS = "2";
    private static final String THIRD_POSITION_POINTS = "1";
    private static final String FOURTH_POSITION_POINTS = "0";
    private static final String DRAW_4 = "1.75";
    private static final String DRAW_FIRST_3 = "2.33";
    private static final String DRAW_LAST_3 = "1";
    private static final String DRAW_FIRST_2 = "3";
    private static final String DRAW_SECOND_2 = "1.5";
    private static final String DRAW_LAST_2 = "0.5";

    public static RankingTable fromGame(Game game, List<Round> rounds) {
        List<PlayerRanking> sortedPlayersRankings = getSortedPlayersRankings(game, rounds);
        List<BestHand> bestHands = getBestHands(rounds);
        return new RankingTable(
                sortedPlayersRankings,
                bestHands.isEmpty() ? "-" : String.valueOf(bestHands.get(0).getHandValue()),
                rounds.size(),
                String.valueOf(rounds.size()),
                game.getPrettyDuration());
    }

    private static List<PlayerRanking> getSortedPlayersRankings(Game game, List<Round> rounds) {
        List<PlayerRanking> playersRankings = setPlayersNamesAndScores(game, rounds);
        Collections.sort(playersRankings);
        Collections.reverse(playersRankings);
        playersRankings = setPlayersTablePoints(playersRankings);
        return playersRankings;
    }

    private static List<PlayerRanking> setPlayersNamesAndScores(Game game, List<Round> rounds) {
        List<PlayerRanking> playersRankings = new ArrayList<>(4);
        int totalScoreP1 = GameRounds.getTotalScoreP1(rounds);
        int totalScoreP2 = GameRounds.getTotalScoreP2(rounds);
        int totalScoreP3 = GameRounds.getTotalScoreP3(rounds);
        int totalScoreP4 = GameRounds.getTotalScoreP4(rounds);
        playersRankings.add(new PlayerRanking(game.getNameP1(), String.valueOf(totalScoreP1)));
        playersRankings.add(new PlayerRanking(game.getNameP2(), String.valueOf(totalScoreP2)));
        playersRankings.add(new PlayerRanking(game.getNameP3(), String.valueOf(totalScoreP3)));
        playersRankings.add(new PlayerRanking(game.getNameP4(), String.valueOf(totalScoreP4)));
        return playersRankings;
    }

    private static List<PlayerRanking> setPlayersTablePoints(
            List<PlayerRanking> sorteredByScorePlayersRankings) {
        sorteredByScorePlayersRankings.get(0).setPoints(FIRST_POSITION_POINTS);
        sorteredByScorePlayersRankings.get(1).setPoints(SECOND_POSITION_POINTS);
        sorteredByScorePlayersRankings.get(2).setPoints(THIRD_POSITION_POINTS);
        sorteredByScorePlayersRankings.get(3).setPoints(FOURTH_POSITION_POINTS);

        String scorePlayerFirst = sorteredByScorePlayersRankings.get(0).getScore();
        String scorePlayerSecond = sorteredByScorePlayersRankings.get(1).getScore();
        String scorePlayerThird = sorteredByScorePlayersRankings.get(2).getScore();
        String scorePlayerFourth = sorteredByScorePlayersRankings.get(3).getScore();

        if(scorePlayerFirst.equals(scorePlayerSecond) &&
                scorePlayerSecond.equals(scorePlayerThird) &&
                scorePlayerThird.equals(scorePlayerFourth)) {
            sorteredByScorePlayersRankings.get(0).setPoints(DRAW_4);
            sorteredByScorePlayersRankings.get(1).setPoints(DRAW_4);
            sorteredByScorePlayersRankings.get(2).setPoints(DRAW_4);
            sorteredByScorePlayersRankings.get(3).setPoints(DRAW_4);
            return sorteredByScorePlayersRankings;
        } else if(scorePlayerFirst.equals(scorePlayerSecond) &&
                scorePlayerSecond.equals(scorePlayerThird)) {
            sorteredByScorePlayersRankings.get(0).setPoints(DRAW_FIRST_3);
            sorteredByScorePlayersRankings.get(1).setPoints(DRAW_FIRST_3);
            sorteredByScorePlayersRankings.get(2).setPoints(DRAW_FIRST_3);
            return sorteredByScorePlayersRankings;
        } else if(scorePlayerSecond.equals(scorePlayerThird) &&
                scorePlayerThird.equals(scorePlayerFourth)) {
            sorteredByScorePlayersRankings.get(1).setPoints(DRAW_LAST_3);
            sorteredByScorePlayersRankings.get(2).setPoints(DRAW_LAST_3);
            sorteredByScorePlayersRankings.get(3).setPoints(DRAW_LAST_3);
            return sorteredByScorePlayersRankings;
        } else {
            if(scorePlayerFirst.equals(scorePlayerSecond)) {
                sorteredByScorePlayersRankings.get(0).setPoints(DRAW_FIRST_2);
                sorteredByScorePlayersRankings.get(1).setPoints(DRAW_FIRST_2);
            }
            if(scorePlayerSecond.equals(scorePlayerThird)) {
                sorteredByScorePlayersRankings.get(1).setPoints(DRAW_SECOND_2);
                sorteredByScorePlayersRankings.get(2).setPoints(DRAW_SECOND_2);
            }
            if(scorePlayerThird.equals(scorePlayerFourth)) {
                sorteredByScorePlayersRankings.get(2).setPoints(DRAW_LAST_2);
                sorteredByScorePlayersRankings.get(3).setPoints(DRAW_LAST_2);
            }
        }
        return sorteredByScorePlayersRankings;
    }

    private static List<BestHand> getBestHands(List<Round> rounds) {
        List<BestHand> bestHands = new ArrayList<>();
        for(Round round: rounds) {
            int roundHandPoints = round.getHandPoints();
            TableWinds roundWinnerInitialPosition = round.getWinnerInitialPosition();
            BestHand bestHand = new BestHand();
            bestHand.setHandValue(roundHandPoints);
            bestHand.setPlayerInitialPosition(round.getWinnerInitialPosition());
            if(bestHands.isEmpty() || roundHandPoints == bestHands.get(0).getHandValue()) {
                bestHands.add(bestHand);
            } else if(roundHandPoints > bestHands.get(0).getHandValue()) {
                bestHands.clear();
                bestHands.add(bestHand);
            }
        }
        return bestHands;
    }
}
