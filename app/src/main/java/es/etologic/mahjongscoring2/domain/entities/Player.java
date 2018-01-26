package es.etologic.mahjongscoring2.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Players",
        indices = { @Index (value = { "name" }, unique = true) })
public class Player {

    @PrimaryKey
    @NonNull
    private final String name;

    @NonNull
    public String getName() {
        return name;
    }

    public Player(@NonNull final String name) {
        this.name = name;
    }
}
