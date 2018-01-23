package es.etologic.mahjongscoring2.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Games")
public class Game {

    private static final int MINUTES_IN_AN_HOUR = 60;

    @PrimaryKey
    private final int gameId;
    private String nameP1;
    private String nameP2;
    private String nameP3;
    private String nameP4;
    private Date startDate;
    private Date endDate;

    public int getGameId() {
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

    public Game(final int gameId, String nameP1, String nameP2, String nameP3, String nameP4,
                Date startDate) {
        this.gameId = gameId;
        this.nameP1 = nameP1;
        this.nameP2 = nameP2;
        this.nameP3 = nameP3;
        this.nameP4 = nameP4;
        this.startDate = startDate;
    }

    public String getPlayerNameByInitialPosition(int initialPosition) {
        switch(initialPosition) {
            case 1: return nameP1;
            case 2: return nameP2;
            case 3: return nameP3;
            case 4: //ToDo: change this int seats by typed enum seats.
            default: return nameP4;
        }
    }
}