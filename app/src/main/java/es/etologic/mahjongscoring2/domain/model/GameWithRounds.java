package es.etologic.mahjongscoring2.domain.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

public class GameWithRounds {

    //CONSTANTS
    private static final long MINUTES_IN_AN_HOUR = 60;

    //FIELDS
    @Embedded public Game game;
    @Relation(parentColumn = "gameId",
              entityColumn = "gameId")
    public List<Round> rounds;

    //GETTERS
    public Game getGame() {
        return game;
    }
    public List<Round> getRounds() {
        return rounds;
    }
    //SETTERS
    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    //CONSTRUCTOR
    public GameWithRounds(Game game) {
        this.game = game;
        this.rounds = new ArrayList<>();
    }

    //METHODS
    public String[] getPlayersTotalPointsString() {
        int[] points = getPlayersTotalPoints();
        return new String[]{
                String.valueOf(points[EAST.getIndex()]),
                String.valueOf(points[SOUTH.getIndex()]),
                String.valueOf(points[WEST.getIndex()]),
                String.valueOf(points[NORTH.getIndex()])
        };
    }

    public int[] getPlayersTotalPoints() {
        int[] points = new int[]{0, 0, 0, 0};
        for (Round round : rounds) {
            points[EAST.getIndex()] += round.getPointsP1();
            points[SOUTH.getIndex()] += round.getPointsP2();
            points[WEST.getIndex()] += round.getPointsP3();
            points[NORTH.getIndex()] += round.getPointsP4();
        }
        return points;
    }

    public BestHand getBestHand() {
        if(rounds == null) {
            return new BestHand();
        } else {
            BestHand bestHand = new BestHand();
            for (Round round : rounds) {
                if (round.getPointsP1() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP1());
                    bestHand.setPlayerName(game.getNameP1());
                }
                if (round.getPointsP2() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP2());
                    bestHand.setPlayerName(game.getNameP2());
                }
                if (round.getPointsP3() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP3());
                    bestHand.setPlayerName(game.getNameP3());
                }
                if (round.getPointsP4() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP4());
                    bestHand.setPlayerName(game.getNameP4());
                }
            }
            return bestHand;
        }
    }

    public long getDuration() {
        if (rounds == null || rounds.isEmpty()) {
            return 0;
        } else {
            long gameDuration = 0;
            for (Round round : rounds) {
                gameDuration += round.getRoundDuration();
            }
            return gameDuration;
        }
    }

    public GameWithRounds getCopy() {
        GameWithRounds gameWithRounds = new GameWithRounds(game.getCopy());
        List<Round> newRounds = new ArrayList<>(rounds.size());
        for(Round round : rounds) {
            newRounds.add(round.getCopy());
        }
        gameWithRounds.setRounds(newRounds);
        return gameWithRounds;
    }

    public String getPrettyDuration() {
        if (rounds != null) {
            long duration = 0;
            for (Round round : rounds) {
                duration += round.getRoundDuration();
            }
            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - (hours*MINUTES_IN_AN_HOUR);
            return String.format(Locale.getDefault(), "%2dh %2dm", hours, minutes);
        } else {
            return "-";
        }
    }
}
