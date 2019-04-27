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

    //FIELDS
    @PrimaryKey(autoGenerate = true) private final int gameId;
    private String nameP1;
    private String nameP2;
    private String nameP3;
    private String nameP4;
    @TypeConverters({DateConverter.class}) private Date creationDate;

    //GETTERS & SETTERS
    public int getGameId() { return gameId; }
    public String getNameP1() { return nameP1; }
    public String getNameP2() { return nameP2; }
    public String getNameP3() { return nameP3; }
    public String getNameP4() { return nameP4; }
    public Date getCreationDate() { return creationDate; }
    public void setNameP1(String nameP1) {
        this.nameP1 = nameP1;
    }
    public void setNameP2(String nameP2) {
        this.nameP2 = nameP2;
    }
    public void setNameP3(String nameP3) {
        this.nameP3 = nameP3;
    }
    public void setNameP4(String nameP4) {
        this.nameP4 = nameP4;
    }

    //CONSTRUCTORS
    public Game(final int gameId, String nameP1, String nameP2, String nameP3, String nameP4, Date creationDate) {
        this.gameId = gameId;
        this.nameP1 = nameP1;
        this.nameP2 = nameP2;
        this.nameP3 = nameP3;
        this.nameP4 = nameP4;
        this.creationDate = creationDate;
    }

    public Game(List<String> playersNames) {
        this.gameId = NOT_SET_GAME_ID;
        this.nameP1 = playersNames.get(EAST.getIndex());
        this.nameP2 = playersNames.get(SOUTH.getIndex());
        this.nameP3 = playersNames.get(WEST.getIndex());
        this.nameP4 = playersNames.get(NORTH.getIndex());
        this.creationDate = Calendar.getInstance().getTime();
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

    public String[] getPlayersNames() {
        String[] names = new String[4];
        names[EAST.getIndex()] = nameP1;
        names[SOUTH.getIndex()] = nameP2;
        names[WEST.getIndex()] = nameP3;
        names[NORTH.getIndex()] = nameP4;
        return names;
    }
    Game getCopy() {
        return new Game(gameId, nameP1, nameP2, nameP3, nameP4, creationDate);
    }
}