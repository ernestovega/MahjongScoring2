package es.etologic.mahjongscoring2.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Players",
        indices = { @Index (value = { "playerName" }, unique = true) })
public class Player {

    @PrimaryKey
    @NonNull
    private final String playerName;

    @NonNull
    public String getPlayerName() {
        return playerName;
    }

    public Player(@NonNull final String playerName) {
        this.playerName = playerName;
    }
}
