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

@Entity(tableName = "Games",
        indices = {@Index(value = {"gameId"},
                          unique = true)})
public class Game {

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

    //METHODS
    public static Game getNewGame(List<String> playersNames) {
        return new Game(NOT_SET_GAME_ID, playersNames.get(0), playersNames.get(1), playersNames.get(2), playersNames.get(3),
                Calendar.getInstance().getTime());
    }

    public String getPlayerNameByInitialPosition(int initialPosition) {
        switch (initialPosition) {
            case 1:
                return nameP1;
            case 2:
                return nameP2;
            case 3:
                return nameP3;
            case 4: //ToDo: change this int seats by typed enum seats.
            default:
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
        names[0] = nameP1;
        names[1] = nameP2;
        names[2] = nameP3;
        names[3] = nameP4;
        return names;
    }

    public String[] getPlayersTotalPoints() {
        int[] points = new int[]{0, 0, 0, 0};
        for (Round round : rounds) {
            points[0] += round.getPointsP1();
            points[1] += round.getPointsP2();
            points[2] += round.getPointsP3();
            points[3] += round.getPointsP4();
        }
        return new String[]{
                String.valueOf(points[0]),
                String.valueOf(points[1]),
                String.valueOf(points[2]),
                String.valueOf(points[3])
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

    public String getPrettyDuration() {
        if (rounds != null) {
            long duration = 0;
            for (Round round : rounds) {
                duration += round.getRoundDuration();
            }
            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - (hours*MINUTES_IN_AN_HOUR);
            String prettyDate = String.format(Locale.getDefault(), "%2dh %2dm", hours, minutes);
            return prettyDate;
        } else {
            return "-";
        }
    }
}