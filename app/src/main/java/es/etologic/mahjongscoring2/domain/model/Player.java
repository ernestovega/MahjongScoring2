package es.etologic.mahjongscoring2.domain.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Players",
        indices = {@Index(value = {"playerName"},
                          unique = true)})
public class Player {

    private static final int NOT_SET_PLAYER_ID = 0;

    @PrimaryKey(autoGenerate = true) private final int playerId;
    @NonNull private final String playerName;

    public int getPlayerId() { return playerId; }
    @NonNull public String getPlayerName() { return playerName; }

    public Player(int playerId, @NonNull final String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public static Player getNewPlayer(String playerName) {
        return new Player(NOT_SET_PLAYER_ID, playerName);
    }
}
