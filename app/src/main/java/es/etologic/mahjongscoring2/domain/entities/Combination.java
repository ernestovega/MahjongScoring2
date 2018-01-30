package es.etologic.mahjongscoring2.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Combinations",
        indices = { @Index( value = { "combinationName" }, unique = true) })
public class Combination {

    @PrimaryKey
    @NonNull
    private String combinationName;
    private String combinationDescription;
    private int combinationPoints;

    @NonNull
    public String getCombinationName() {
        return combinationName;
    }

    public String getCombinationDescription() {
        return combinationDescription;
    }

    public int getCombinationPoints() {
        return combinationPoints;
    }

    public Combination(@NonNull String combinationName, String combinationDescription,
                       int combinationPoints) {
        this.combinationName = combinationName;
        this.combinationDescription = combinationDescription;
        this.combinationPoints = combinationPoints;
    }
}
