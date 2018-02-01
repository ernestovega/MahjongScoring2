package es.etologic.mahjongscoring2.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.etologic.mahjongscoring2.data.repository.local.converters.DateConverter;

@Entity(tableName = "Games",
        indices = { @Index ( value = { "gameId" }, unique = true) })
public class Game {

    public static final int NOT_SET_GAME_ID = 0;

    @PrimaryKey(autoGenerate = true)
    private final long gameId;
    private String nameP1;
    private String nameP2;
    private String nameP3;
    private String nameP4;
    @TypeConverters({DateConverter.class})
    private Date startDate;
    @TypeConverters({DateConverter.class})
    private Date endDate;
    @Ignore
    private List<Round> rounds;

    //region Getters & Setters

    public long getGameId() {
        return gameId;
    }

    public String getNameP1() {
        return nameP1;
    }

    public String getNameP2() {
        return nameP2;
    }

    public String getNameP3() {
        return nameP3;
    }

    public String getNameP4() {
        return nameP4;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    //endregion

    public Game(final long gameId, String nameP1, String nameP2, String nameP3, String nameP4,
                Date startDate) {
        this.gameId = gameId;
        this.nameP1 = nameP1;
        this.nameP2 = nameP2;
        this.nameP3 = nameP3;
        this.nameP4 = nameP4;
        this.startDate = startDate;
        this.rounds = new ArrayList<>();
    }

    //region Methods

    public String getPlayerNameByInitialPosition(int initialPosition) {
        switch(initialPosition) {
            case 1: return nameP1;
            case 2: return nameP2;
            case 3: return nameP3;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return nameP4;
        }
    }

    public BestHand getBestHand() {
        BestHand bestHand = new BestHand();
        for(Round round : rounds) {
            if(round.getPointsP1() > bestHand.getHandValue()) {
                bestHand.setHandValue(round.getPointsP1());
                bestHand.setPlayerName(nameP1);
            }
            if(round.getPointsP2() > bestHand.getHandValue()) {
                bestHand.setHandValue(round.getPointsP2());
                bestHand.setPlayerName(nameP2);
            }
            if(round.getPointsP3() > bestHand.getHandValue()) {
                bestHand.setHandValue(round.getPointsP3());
                bestHand.setPlayerName(nameP3);
            }
            if(round.getPointsP4() > bestHand.getHandValue()) {
                bestHand.setHandValue(round.getPointsP4());
                bestHand.setPlayerName(nameP4);
            }
        }
        return bestHand;
    }

    public String[] getPlayersTotalPoints() {
        int[] points = new int[]{0, 0, 0, 0};
        for(Round round : rounds) {
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

    public Game getCopy() {
        Game gameCopy = new Game(gameId, nameP1, nameP2, nameP3, nameP4, startDate);
        gameCopy.setEndDate(endDate);
        gameCopy.setRounds(rounds);
        return gameCopy;
    }

    //endregion
}