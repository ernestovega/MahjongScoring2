package es.etologic.mahjongscoring2.domain.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.RecyclerViewable;

@Entity(tableName = "Rounds",
        primaryKeys = { "gameId", "roundId" },
        foreignKeys = { @ForeignKey (
                entity = Game.class,
                parentColumns = "gameId",
                childColumns = "gameId") },
        indices = { @Index (value = { "gameId", "roundId" }, unique = true) })
public class Round extends RecyclerViewable<Round> {

    //Constants
    private static final int HU_BASE_POINTS = 8;
    private static final int NUM_NO_WINNER_PLAYERS_IN_TSUMO = 3;
    private static final int NUM_NO_WINNER_AND_NO_LOOSER_PLAYERS_IN_RON = 2;

    //Fields
    private final int gameId;
    private final int roundId;
    private int handPoints;
    private int winnerInitialPosition; //ToDo: change this int seats by typed enum seats.
    private int looserInitialPosition;//ToDo: change this int seats by typed enum seats.
    private int pointsP1;
    private int pointsP2;
    private int pointsP3;
    private int pointsP4;
    private int penaltyP1;
    private int penaltyP2;
    private int penaltyP3;
    private int penaltyP4;
    private long roundDuration;

    //region GETTERS & SETTERS
    public int getGameId() {
        return gameId;
    }
    public int getRoundId() {
        return roundId;
    }
    public int getHandPoints() {
        return handPoints;
    }
    public void setHandPoints(int handPoints) {
        this.handPoints = handPoints;
    }
    public int getWinnerInitialPosition() {
        return winnerInitialPosition;
    }
    public void setWinnerInitialPosition(int winnerInitialPosition) {
        this.winnerInitialPosition = winnerInitialPosition;
    }
    public int getLooserInitialPosition() {
        return looserInitialPosition;
    }
    public void setLooserInitialPosition(int looserInitialPosition) {
        this.looserInitialPosition = looserInitialPosition;
    }
    public int getPointsP1() {
        return pointsP1;
    }
    public void setPointsP1(int pointsP1) {
        this.pointsP1 = pointsP1;
    }
    public int getPointsP2() {
        return pointsP2;
    }
    public void setPointsP2(int pointsP2) {
        this.pointsP2 = pointsP2;
    }
    public int getPointsP3() {
        return pointsP3;
    }
    public void setPointsP3(int pointsP3) {
        this.pointsP3 = pointsP3;
    }
    public int getPointsP4() {
        return pointsP4;
    }
    public void setPointsP4(int pointsP4) {
        this.pointsP4 = pointsP4;
    }
    public int getPenaltyP1() {
        return penaltyP1;
    }
    public void setPenaltyP1(int penaltyP1) {
        this.penaltyP1 = penaltyP1;
    }
    public int getPenaltyP2() {
        return penaltyP2;
    }
    public void setPenaltyP2(int penaltyP2) {
        this.penaltyP2 = penaltyP2;
    }
    public int getPenaltyP3() {
        return penaltyP3;
    }
    public void setPenaltyP3(int penaltyP3) {
        this.penaltyP3 = penaltyP3;
    }
    public int getPenaltyP4() {
        return penaltyP4;
    }
    public void setPenaltyP4(int penaltyP4) {
        this.penaltyP4 = penaltyP4;
    }
    public long getRoundDuration() {
        return roundDuration;
    }
    public void setRoundDuration(long roundDuration) {
        this.roundDuration = roundDuration;
    }

    //Constructors
    public Round(final int gameId, final int roundId) {
        this.gameId = gameId;
        this.roundId = roundId;
    }
    private Round(int gameId, int roundId, int handPoints, int winnerInitialPosition, int looserInitialPosition, int pointsP1, int pointsP2,
                  int pointsP3, int pointsP4, int penaltyP1, int penaltyP2, int penaltyP3, int penaltyP4, long roundDuration) {
        this.gameId = gameId;
        this.roundId = roundId;
        this.handPoints = handPoints;
        this.winnerInitialPosition = winnerInitialPosition;
        this.looserInitialPosition = looserInitialPosition;
        this.pointsP1 = pointsP1;
        this.pointsP2 = pointsP2;
        this.pointsP3 = pointsP3;
        this.pointsP4 = pointsP4;
        this.penaltyP1 = penaltyP1;
        this.penaltyP2 = penaltyP2;
        this.penaltyP3 = penaltyP3;
        this.penaltyP4 = penaltyP4;
        this.roundDuration = roundDuration;
    }

    //Methods
    public void setAllPlayersTsumoPoints(int winnerInitialPosition, int winnerHandPoints) {
        this.winnerInitialPosition = winnerInitialPosition;
        this.handPoints = winnerHandPoints;
        int looserTotalPoints = winnerHandPoints + HU_BASE_POINTS;
        int winnerTotalPoints = (looserTotalPoints) * NUM_NO_WINNER_PLAYERS_IN_TSUMO;
        pointsP1 += (1 == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
        pointsP2 += (2 == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
        pointsP3 += (3 == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
        pointsP4 += (4 == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
    }
    public void setAllPlayersRonPoints(int winnerInitialPosition, int winnerHandPoints, int looserInitialPosition) {
        this.winnerInitialPosition = winnerInitialPosition;
        this.handPoints = winnerHandPoints;
        this.looserInitialPosition = looserInitialPosition;
        int looserTotalPoints = winnerHandPoints + HU_BASE_POINTS;
        int winnerTotalPoints = looserTotalPoints +
                (HU_BASE_POINTS * NUM_NO_WINNER_AND_NO_LOOSER_PLAYERS_IN_RON);
        if(1 == winnerInitialPosition) {
            pointsP1 += winnerTotalPoints;
        } else {
            pointsP1 -= (1 == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
        if(2 == winnerInitialPosition) {
            pointsP2 += winnerTotalPoints;
        } else {
            pointsP2 -= (3 == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
        if(3 == winnerInitialPosition) {
            pointsP3 += winnerTotalPoints;
        } else {
            pointsP3 -= (3 == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
        if(4 == winnerInitialPosition) {
            pointsP4 += winnerTotalPoints;
        } else {
            pointsP4 -= (4 == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
    }
    public void setAllPlayersPointsByPenalty(int playerInitialPosition, int penaltyPoints) {
        int noPenalizedPlayerPoints = penaltyPoints / NUM_NO_WINNER_PLAYERS_IN_TSUMO;
        penaltyP1 += (1 == playerInitialPosition) ? penaltyPoints : 0;
        penaltyP2 += (2 == playerInitialPosition) ? penaltyPoints : 0;
        penaltyP3 += (3 == playerInitialPosition) ? penaltyPoints : 0;
        penaltyP4 += (4 == playerInitialPosition) ? penaltyPoints : 0;
        pointsP1 += (1 == playerInitialPosition) ? -penaltyPoints : noPenalizedPlayerPoints;
        pointsP2 += (2 == playerInitialPosition) ? -penaltyPoints : noPenalizedPlayerPoints;
        pointsP3 += (3 == playerInitialPosition) ? -penaltyPoints : noPenalizedPlayerPoints;
        pointsP4 += (4 == playerInitialPosition) ? -penaltyPoints : noPenalizedPlayerPoints;
    }
    public void setAllPlayersPointsByPenaltyCancellation(int playerInitialPosition) {
        int penaltyPoints = getPenaltyPointsFromInitialPlayerPosition(playerInitialPosition);
        int noPenalizedPlayerPoints = penaltyPoints / NUM_NO_WINNER_PLAYERS_IN_TSUMO;
        if(1 == playerInitialPosition) { penaltyP1 = 0; }
        if(2 == playerInitialPosition) { penaltyP2 = 0; }
        if(3 == playerInitialPosition) { penaltyP3 = 0; }
        if(4 == playerInitialPosition) { penaltyP4 = 0; }
        pointsP1 += (1 == playerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;
        pointsP2 += (2 == playerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;
        pointsP3 += (3 == playerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;
        pointsP4 += (4 == playerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;

    }
    public boolean isPenalizedPlayer(int playerInitialPosition) {
        switch(playerInitialPosition) {
            case 1: return penaltyP1 > 0;
            case 2: return penaltyP2 > 0;
            case 3: return penaltyP3 > 0;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return penaltyP4 > 0;
        }
    }
    private int getPenaltyPointsFromInitialPlayerPosition(int playerInitialPosition) {
        switch(playerInitialPosition) {
            case 1: return penaltyP1;
            case 2: return penaltyP2;
            case 3: return penaltyP3;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return penaltyP4;
        }
    }
    public static int getEastSeatPlayerByRound(int roundId) {
        switch(roundId) {
            case 1:case 2:case 3:case 4:default: return 1;
            case 5:case 6:case 7:case 8:         return 2;
            case 9:case 10:case 11:case 12:      return 3;
            case 13:case 14:case 15:case 16:     return 4;
        } //ToDo: change this returned int seats by typed enum seats.
    }
    public static int getSouthSeatPlayerByRound(int roundId) {
        switch(roundId) {
            case 1:case 2:case 3:case 4:default: return 2;
            case 5:case 6:case 7:case 8:         return 1;
            case 9:case 10:case 11:case 12:      return 4;
            case 13:case 14:case 15:case 16:     return 3;
        } //ToDo: change this returned int seats by typed enum seats.
    }
    public static int getWestSeatPlayerByRound(int roundId) {
        switch(roundId) {
            case 1:case 2:case 3:case 4:default: return 3;
            case 5:case 6:case 7:case 8:         return 4;
            case 9:case 10:case 11:case 12:      return 2;
            case 13:case 14:case 15:case 16:     return 1;
        } //ToDo: change this returned int seats by typed enum seats.
    }
    public static int getNorthSeatPlayerByRound(int roundId) {
        switch(roundId) {
            case 1:case 2:case 3:case 4:default: return 4;
            case 5:case 6:case 7:case 8:         return 3;
            case 9:case 10:case 11:case 12:      return 1;
            case 13:case 14:case 15:case 16:     return 2;
        } //ToDo: change this returned int seats by typed enum seats.
    }
    public static int getPlayerInitialPositionBySeat(int seat, int roundId) {
        switch(roundId) {
            case 1:case 2:case 3:case 4:
                return getPlayerInitialPositionBySeatInRoundEast(seat);
            case 5:case 6:case 7:case 8:
                return getPlayerInitialPositionBySeatInRoundSouth(seat);
            case 9:case 10:case 11:case 12:
                return getPlayerInitialPositionBySeatInRoundWest(seat);
            case 13:case 14:case 15:case 16: default:
                return getPlayerInitialPositionBySeatInRoundNorth(seat);
            //ToDo: change this int seats by typed enum seats.
        }
    }
    private static int getPlayerInitialPositionBySeatInRoundEast(int seat) {
        switch(seat) {
            case 1: return 1;
            case 2: return 2;
            case 3: return 3;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return 4;
        }
    }
    private static int getPlayerInitialPositionBySeatInRoundSouth(int seat) {
        switch(seat) {
            case 1: return 2;
            case 2: return 1;
            case 3: return 4;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return 3;
        }
    }
    private static int getPlayerInitialPositionBySeatInRoundWest(int seat) {
        switch(seat) {
            case 1: return 3;
            case 2: return 4;
            case 3: return 2;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return 1;
        }
    }
    private static int getPlayerInitialPositionBySeatInRoundNorth(int seat) {
        switch(seat) {
            case 1: return 4;
            case 2: return 3;
            case 3: return 1;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return 2;
        }
    }
    private static boolean areEqual(Round round1, Round round2) {
        return round1.gameId == round2.gameId &&
                round1.roundId == round2.roundId &&
                round1.handPoints == round2.handPoints &&
                round1.winnerInitialPosition == round2.winnerInitialPosition &&
                round1.looserInitialPosition == round2.looserInitialPosition &&
                round1.pointsP1 == round2.pointsP1 &&
                round1.pointsP2 == round2.pointsP2 &&
                round1.pointsP3 == round2.pointsP3 &&
                round1.pointsP4 == round2.pointsP4 &&
                round1.penaltyP1 == round2.penaltyP1 &&
                round1.penaltyP2 == round2.penaltyP2 &&
                round1.penaltyP3 == round2.penaltyP3 &&
                round1.penaltyP4 == round2.penaltyP4 &&
                round1.roundDuration == round2.roundDuration;
    }
    public static boolean areEqual(List<Round> rounds1, List<Round> rounds2) {
        if(rounds1 == null && rounds2 == null) {
            return true;
        } else if(rounds1 != null && rounds2 != null) {
            if(rounds1.size() != rounds2.size()) {
                return false;
            } else {
                for (int i = 0; i < rounds1.size(); i++) {
                    if(!areEqual(rounds1.get(i), rounds2.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    //RecyclerViewable
    @Override public boolean compareIdTo(Round object) {
        return gameId == object.getGameId() &&
                roundId == object.getRoundId();
    }
    @Override public boolean compareContentsTo(Round object) {
        return areEqual(this, object);
    }
    @Override public Round getCopy() {
        return new Round(
                gameId,
                roundId,
                handPoints,
                winnerInitialPosition,
                looserInitialPosition,
                pointsP1,
                pointsP2,
                pointsP3,
                pointsP4,
                penaltyP1,
                penaltyP2,
                penaltyP3,
                penaltyP4,
                roundDuration
        );
    }
}