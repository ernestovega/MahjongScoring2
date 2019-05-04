package es.etologic.mahjongscoring2.domain.model;

import java.util.List;

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

public class GameRounds {

    public static int getTotalScoreP1(List<Round> rounds) {
        int totalPoints = 0;
        for (Round round : rounds) totalPoints += round.getPointsP1();
        return totalPoints;
    }

    public static int getTotalScoreP2(List<Round> rounds) {
        int totalPoints = 0;
        for (Round round : rounds) totalPoints += round.getPointsP2();
        return totalPoints;
    }

    public static int getTotalScoreP3(List<Round> rounds) {
        int totalPoints = 0;
        for (Round round : rounds) totalPoints += round.getPointsP3();
        return totalPoints;
    }

    public static int getTotalScoreP4(List<Round> rounds) {
        int totalPoints = 0;
        for (Round round : rounds) totalPoints += round.getPointsP4();
        return totalPoints;
    }

    public static TableWinds getEastPlayerCurrentSeat(int roundId) {
        switch (roundId) {
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                return EAST;
            case 5:
            case 6:
            case 7:
            case 8:
                return SOUTH;
            case 9:
            case 10:
            case 11:
            case 12:
                return WEST;
            case 13:
            case 14:
            case 15:
            case 16:
                return NORTH;
        }
    }

    public static TableWinds getSouthSeatPlayerByRound(int roundId) {
        switch (roundId) {
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                return SOUTH;
            case 5:
            case 6:
            case 7:
            case 8:
                return EAST;
            case 9:
            case 10:
            case 11:
            case 12:
                return NORTH;
            case 13:
            case 14:
            case 15:
            case 16:
                return WEST;
        }
    }

    public static TableWinds getWestSeatPlayerByRound(int roundId) {
        switch (roundId) {
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                return WEST;
            case 5:
            case 6:
            case 7:
            case 8:
                return NORTH;
            case 9:
            case 10:
            case 11:
            case 12:
                return SOUTH;
            case 13:
            case 14:
            case 15:
            case 16:
                return EAST;
        }
    }

    public static TableWinds getNorthSeatPlayerByRound(int roundId) {
        switch (roundId) {
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                return NORTH;
            case 5:
            case 6:
            case 7:
            case 8:
                return WEST;
            case 9:
            case 10:
            case 11:
            case 12:
                return EAST;
            case 13:
            case 14:
            case 15:
            case 16:
                return SOUTH;
        }
    }

    public static TableWinds getPlayerInitialSeatByCurrentSeat(TableWinds seatPosition, int roundId) {
        switch (roundId) {
            case 1:
            case 2:
            case 3:
            case 4:
                return getPlayerInitialPositionBySeatInRoundEast(seatPosition);
            case 5:
            case 6:
            case 7:
            case 8:
                return getPlayerInitialPositionBySeatInRoundSouth(seatPosition);
            case 9:
            case 10:
            case 11:
            case 12:
                return getPlayerInitialPositionBySeatInRoundWest(seatPosition);
            case 13:
            case 14:
            case 15:
            case 16:
            default:
                return getPlayerInitialPositionBySeatInRoundNorth(seatPosition);

        }
    }

    private static TableWinds getPlayerInitialPositionBySeatInRoundEast(TableWinds seatPosition) {
        switch (seatPosition) {
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case NORTH:
            default:
                return NORTH;
        }
    }

    private static TableWinds getPlayerInitialPositionBySeatInRoundSouth(TableWinds seatPosition) {
        switch (seatPosition) {
            case EAST:
                return SOUTH;
            case SOUTH:
                return EAST;
            case WEST:
                return NORTH;
            case NORTH:
            default:
                return WEST;
        }
    }

    private static TableWinds getPlayerInitialPositionBySeatInRoundWest(TableWinds seatPosition) {
        switch (seatPosition) {
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return SOUTH;
            case NORTH:
            default:
                return EAST;
        }
    }

    private static TableWinds getPlayerInitialPositionBySeatInRoundNorth(TableWinds seatPosition) {
        switch (seatPosition) {
            case EAST:
                return NORTH;
            case SOUTH:
                return WEST;
            case WEST:
                return EAST;
            case NORTH:
            default:
                return SOUTH;
        }
    }
}
