package es.etologic.mahjongscoring2.domain.model;

import java.util.List;

public class GameRounds {

    public static int getTotalScoreP1(List<Round> rounds) {
        int totalPoints = 0;
        for(Round round : rounds) totalPoints += round.getPointsP1();
        return totalPoints;
    }

    public static int getTotalScoreP2(List<Round> rounds) {
        int totalPoints = 0;
        for(Round round : rounds) totalPoints += round.getPointsP2();
        return totalPoints;
    }

    public static int getTotalScoreP3(List<Round> rounds) {
        int totalPoints = 0;
        for(Round round : rounds) totalPoints += round.getPointsP3();
        return totalPoints;
    }

    public static int getTotalScoreP4(List<Round> rounds) {
        int totalPoints = 0;
        for(Round round : rounds) totalPoints += round.getPointsP4();
        return totalPoints;
    }
}
