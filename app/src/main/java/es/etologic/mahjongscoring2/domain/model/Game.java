package es.etologic.mahjongscoring2.domain.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.etologic.mahjongscoring2.data.local_data_source.local.converters.DateConverter;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

@Entity(tableName = "Games",
        indices = {@Index(value = {"gameId"},
                          unique = true)})
public class Game extends GameRounds{

    //CONSTANTS
    private static final int NOT_SET_GAME_ID = 0;
    private static final long MINUTES_IN_AN_HOUR = 60;

    //FIELDS
    @PrimaryKey(autoGenerate = true) private final int gameId;
    private String nameP1;
    private String nameP2;
    private String nameP3;
    private String nameP4;
    @TypeConverters({DateConverter.class}) private Date creationDate;
    @Ignore private List<Round> rounds;

    //GETTERS & SETTERS
    public int getGameId() { return gameId; }
    public String getNameP1() { return nameP1; }
    public String getNameP2() { return nameP2; }
    public String getNameP3() { return nameP3; }
    public String getNameP4() { return nameP4; }
    public Date getCreationDate() { return creationDate; }
    public List<Round> getRounds() { return rounds; }
    public void setRounds(List<Round> rounds) { this.rounds = rounds; }

    //CONSTRUCTORS
    public Game(final int gameId, String nameP1, String nameP2, String nameP3, String nameP4, Date creationDate) {
        this.gameId = gameId;
        this.nameP1 = nameP1;
        this.nameP2 = nameP2;
        this.nameP3 = nameP3;
        this.nameP4 = nameP4;
        this.creationDate = creationDate;
        this.rounds = new ArrayList<>();
    }

    public Game(List<String> playersNames) {
        this.gameId = NOT_SET_GAME_ID;
        this.nameP1 = playersNames.get(EAST.getIndex());
        this.nameP2 = playersNames.get(SOUTH.getIndex());
        this.nameP3 = playersNames.get(WEST.getIndex());
        this.nameP4 = playersNames.get(NORTH.getIndex());
        this.creationDate = Calendar.getInstance().getTime();
        this.rounds = new ArrayList<>();
    }

    public String getPlayerNameByInitialPosition(TableWinds initialPosition) {
        switch (initialPosition) {
            default:
            case NONE:
                return "";
            case EAST:
                return nameP1;
            case SOUTH:
                return nameP2;
            case WEST:
                return nameP3;
            case NORTH:
                return nameP4;
        }
    }

    public BestHand getBestHand() {
        if(rounds == null) {
            return new BestHand();
        } else {
            BestHand bestHand = new BestHand();
            for (Round round : rounds) {
                if (round.getPointsP1() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP1());
                    bestHand.setPlayerName(nameP1);
                }
                if (round.getPointsP2() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP2());
                    bestHand.setPlayerName(nameP2);
                }
                if (round.getPointsP3() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP3());
                    bestHand.setPlayerName(nameP3);
                }
                if (round.getPointsP4() > bestHand.getHandValue()) {
                    bestHand.setHandValue(round.getPointsP4());
                    bestHand.setPlayerName(nameP4);
                }
            }
            return bestHand;
        }
    }

    public String[] getPlayersNames() {
        String[] names = new String[4];
        names[EAST.getIndex()] = nameP1;
        names[SOUTH.getIndex()] = nameP2;
        names[WEST.getIndex()] = nameP3;
        names[NORTH.getIndex()] = nameP4;
        return names;
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

    public String[] getPlayersTotalPointsString() {
        int[] points = getPlayersTotalPoints();
        return new String[]{
                String.valueOf(points[EAST.getIndex()]),
                String.valueOf(points[SOUTH.getIndex()]),
                String.valueOf(points[WEST.getIndex()]),
                String.valueOf(points[NORTH.getIndex()])
        };
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

    public Game getCopy() {
        Game gameCopy = new Game(gameId, nameP1, nameP2, nameP3, nameP4, creationDate);
        gameCopy.setRounds(rounds);
        return gameCopy;
    }

    String getPrettyDuration() {
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