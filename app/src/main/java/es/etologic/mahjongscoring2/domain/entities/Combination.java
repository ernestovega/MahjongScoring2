package es.etologic.mahjongscoring2.domain.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.local_data_source.local.converters.CombinationDescriptionTypeConverter;

import static es.etologic.mahjongscoring2.domain.entities.Combination.CombinationDescriptionType.DESCRIPTION;
import static es.etologic.mahjongscoring2.domain.entities.Combination.CombinationDescriptionType.IMAGE;

@Entity(tableName = "Combinations",
        indices = { @Index ( value = { "combinationName" }, unique = true) })
public class Combination {

    public enum CombinationDescriptionType {
        IMAGE(0),
        DESCRIPTION(1);

        private int code;

        CombinationDescriptionType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private final int combinationPoints;

    @PrimaryKey(autoGenerate = true)
    private final long combinationId;

    @NonNull
    private final String combinationName;
    private final @DrawableRes Integer combinationImage;
    private final String combinationDescription;
    @TypeConverters(CombinationDescriptionTypeConverter.class)
    private final CombinationDescriptionType combinationDescriptionType;

    public int getCombinationPoints() {
        return combinationPoints;
    }

    @NonNull
    public String getCombinationName() {
        return combinationName;
    }

    public Integer getCombinationImage() {
        return combinationImage;
    }

    public String getCombinationDescription() {
        return combinationDescription;
    }

    public CombinationDescriptionType getCombinationDescriptionType() {
        return combinationDescriptionType;
    }

    public Combination(int combinationPoints, long combinationId, @NonNull String combinationName,
                       Integer combinationImage, String combinationDescription,
                       CombinationDescriptionType combinationDescriptionType) {
        this.combinationPoints = combinationPoints;
        this.combinationId = combinationId;
        this.combinationName = combinationName;
        this.combinationImage = combinationImage;
        this.combinationDescription = combinationDescription;
        this.combinationDescriptionType = combinationDescriptionType;
    }

    @Ignore
    public Combination(int combinationPoints, long combinationId, String combinationName, Integer combinationImage) {
        this.combinationPoints = combinationPoints;
        this.combinationId = combinationId;
        this.combinationName = combinationName;
        this.combinationImage = combinationImage;
        this.combinationDescription = null;
        combinationDescriptionType = IMAGE;
    }

    @Ignore
    public Combination(int combinationPoints, long combinationId, String combinationName, String combinationDescription) {
        this.combinationPoints = combinationPoints;
        this.combinationId = combinationId;
        this.combinationName = combinationName;
        this.combinationImage = null;
        this.combinationDescription = combinationDescription;
        combinationDescriptionType = DESCRIPTION;
    }

    public Combination getCopy() {
        if(combinationDescriptionType == IMAGE) {
            return new Combination(combinationPoints, combinationId, combinationName, combinationImage);
        } else {
            return new Combination(combinationPoints, combinationId, combinationName, combinationDescription);
        }
    }
}