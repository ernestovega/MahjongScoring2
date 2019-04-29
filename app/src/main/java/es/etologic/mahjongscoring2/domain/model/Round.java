package es.etologic.mahjongscoring2.domain.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.RecyclerViewable;
import es.etologic.mahjongscoring2.data.local_data_source.local.converters.TableWindsConverter;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

@Entity(tableName = "Rounds",
        primaryKeys = {"gameId", "roundId"},
        foreignKeys = {@ForeignKey(
                entity = Game.class,
                parentColumns = "gameId",
                childColumns = "gameId")},
        indices = {@Index(value = {"gameId", "roundId"},
                          unique = true)})
public class Round extends RecyclerViewable<Round> {

    //Constants
    private static final int HU_BASE_POINTS = 8;
    private static final int NUM_NO_WINNER_PLAYERS = 3;
    private static final int NUM_NO_WINNER_AND_NO_LOOSER_PLAYERS_IN_RON = 2;

    //Fields
    private final int gameId;
    private final int roundId;
    private int handPoints = 0;
    @TypeConverters(TableWindsConverter.class)
    private TableWinds winnerInitialPosition = NONE;
    @TypeConverters(TableWindsConverter.class)
    private TableWinds discarderInitialPosition = NONE;
    private int pointsP1 = 0;
    private int pointsP2 = 0;
    private int pointsP3 = 0;
    private int pointsP4 = 0;
    private int penaltyP1 = 0;
    private int penaltyP2 = 0;
    private int penaltyP3 = 0;
    private int penaltyP4 = 0;
    private long roundDuration = 0;
    @Ignore
    private boolean isBestHand = false;
    public int getHandPoints() {
        return handPoints;
    }
    public void setHandPoints(int handPoints) {
        this.handPoints = handPoints;
    }
    public TableWinds getWinnerInitialPosition() {
        return winnerInitialPosition;
    }
    public void setWinnerInitialPosition(TableWinds winnerInitialPosition) {
        this.winnerInitialPosition = winnerInitialPosition;
    }
    public TableWinds getDiscarderInitialPosition() {
        return discarderInitialPosition;
    }
    public void setDiscarderInitialPosition(TableWinds looserInitialPosition) {
        this.discarderInitialPosition = looserInitialPosition;
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
    public boolean isBestHand() {
        return isBestHand;
    }
    public void setIsBestHand(boolean bestHand) {
        isBestHand = bestHand;
    }
    //Constructors
    public Round(final int gameId, final int roundId) {
        this.gameId = gameId;
        this.roundId = roundId;

    }
    private Round(int gameId, int roundId, int handPoints, TableWinds winnerInitialPosition, TableWinds discarderInitialPosition, int pointsP1, int pointsP2,
                  int pointsP3, int pointsP4, int penaltyP1, int penaltyP2, int penaltyP3, int penaltyP4, long roundDuration) {
        this.gameId = gameId;
        this.roundId = roundId;
        this.handPoints = handPoints;
        this.winnerInitialPosition = winnerInitialPosition;
        this.discarderInitialPosition = discarderInitialPosition;
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
    public void setAllPlayersTsumoPoints(TableWinds winnerInitialPosition, int winnerHandPoints) {
        this.winnerInitialPosition = winnerInitialPosition;
        this.handPoints = winnerHandPoints;
        int looserTotalPoints = winnerHandPoints + HU_BASE_POINTS;
        int winnerTotalPoints = (looserTotalPoints)*NUM_NO_WINNER_PLAYERS;
        pointsP1 += (EAST == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
        pointsP2 += (SOUTH == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
        pointsP3 += (WEST == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
        pointsP4 += (NORTH == winnerInitialPosition) ? winnerTotalPoints : -looserTotalPoints;
    }
    public void setAllPlayersRonPoints(TableWinds winnerInitialPosition, int winnerHandPoints, TableWinds looserInitialPosition) {
        this.winnerInitialPosition = winnerInitialPosition;
        this.handPoints = winnerHandPoints;
        this.discarderInitialPosition = looserInitialPosition;
        int looserTotalPoints = winnerHandPoints + HU_BASE_POINTS;
        int winnerTotalPoints = looserTotalPoints +
                (HU_BASE_POINTS*NUM_NO_WINNER_AND_NO_LOOSER_PLAYERS_IN_RON);
        if (EAST == winnerInitialPosition) {
            pointsP1 += winnerTotalPoints;
        } else {
            pointsP1 -= (EAST == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
        if (SOUTH == winnerInitialPosition) {
            pointsP2 += winnerTotalPoints;
        } else {
            pointsP2 -= (SOUTH == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
        if (WEST == winnerInitialPosition) {
            pointsP3 += winnerTotalPoints;
        } else {
            pointsP3 -= (WEST == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
        if (NORTH == winnerInitialPosition) {
            pointsP4 += winnerTotalPoints;
        } else {
            pointsP4 -= (NORTH == looserInitialPosition) ? looserTotalPoints : HU_BASE_POINTS;
        }
    }
    public void setPlayerPenaltyPoints(TableWinds penalizedPlayerInitialPosition, int penaltyPoints) {
        switch (penalizedPlayerInitialPosition) {
            case EAST:
                penaltyP1 -= penaltyPoints;
                break;
            case SOUTH:
                penaltyP2 -= penaltyPoints;
                break;
            case WEST:
                penaltyP3 -= penaltyPoints;
                break;
            default:
                penaltyP4 -= penaltyPoints;
                break;
        }
    }
    public void setAllPlayersPenaltyPoints(TableWinds penalizedPlayerInitialPosition, int penaltyPoints) {
        int noPenalizedPlayerPoints = penaltyPoints/NUM_NO_WINNER_PLAYERS;
        penaltyP1 -= (EAST == penalizedPlayerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;
        penaltyP2 -= (SOUTH == penalizedPlayerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;
        penaltyP3 -= (WEST == penalizedPlayerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;
        penaltyP4 -= (NORTH == penalizedPlayerInitialPosition) ? penaltyPoints : -noPenalizedPlayerPoints;
    }
    public void applyAllPlayersPenalties() {
        pointsP1 += penaltyP1;
        pointsP2 += penaltyP2;
        pointsP3 += penaltyP3;
        pointsP4 += penaltyP4;
    }
    public void cancelAllPlayersPenalties() {
        penaltyP1 = 0;
        penaltyP2 = 0;
        penaltyP3 = 0;
        penaltyP4 = 0;
    }
    public boolean isPenalizedPlayer(TableWinds playerInitialPosition) {
        switch (playerInitialPosition) {
            case EAST:
                return penaltyP1 < 0;
            case SOUTH:
                return penaltyP2 < 0;
            case WEST:
                return penaltyP3 < 0;
            default:
                return penaltyP4 < 0;
        }
    }
    private int getPenaltyPointsFromInitialPlayerPosition(TableWinds playerInitialPosition) {
        switch (playerInitialPosition) {
            case EAST:
                return penaltyP1;
            case SOUTH:
                return penaltyP2;
            case WEST:
                return penaltyP3;
            default:
                return penaltyP4;
        }
    }
    public static boolean areEqual(List<Round> rounds1, List<Round> rounds2) {
        if (rounds1 == null && rounds2 == null) {
            return true;
        } else if (rounds1 != null && rounds2 != null) {
            if (rounds1.size() != rounds2.size()) {
                return false;
            } else {
                for (int i = 0; i < rounds1.size(); i++) {
                    if (!areEqual(rounds1.get(i), rounds2.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }
    private static boolean areEqual(Round round1, Round round2) {
        return round1.gameId == round2.gameId &&
                round1.roundId == round2.roundId &&
                round1.handPoints == round2.handPoints &&
                round1.winnerInitialPosition == round2.winnerInitialPosition &&
                round1.discarderInitialPosition == round2.discarderInitialPosition &&
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
    //RecyclerViewable
    @Override public boolean compareIdTo(Round object) {
        return gameId == object.getGameId() &&
                roundId == object.getRoundId();
    }
    //region GETTERS & SETTERS
    public int getGameId() {
        return gameId;
    }
    public int getRoundId() {
        return roundId;
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
                discarderInitialPosition,
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
    public int[] getPlayersPenalties() {
        return new int[]{penaltyP1, penaltyP2, penaltyP3, penaltyP4};
    }
}