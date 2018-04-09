package es.etologic.mahjongscoring2.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Players",
        indices = { @Index (value = { "playerName" }, unique = true) })
public class Player {

    @PrimaryKey(autoGenerate = true)
    private final long playerId;

    @NonNull
    private final String playerName;

    public long getPlayerId() { return playerId; }
    @NonNull
    public String getPlayerName() {
        return playerName;
    }

    public Player(long playerId, @NonNull final String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }
}
