package es.etologic.mahjongscoring2.data.local_data_source.local.converters;

import android.arch.persistence.room.TypeConverter;

import es.etologic.mahjongscoring2.domain.entities.Combination.CombinationDescriptionType;

import static es.etologic.mahjongscoring2.domain.entities.Combination.CombinationDescriptionType.DESCRIPTION;
import static es.etologic.mahjongscoring2.domain.entities.Combination.CombinationDescriptionType.IMAGE;

public class CombinationDescriptionTypeConverter {

    @TypeConverter
    public static CombinationDescriptionType toCombinationDescriptionType(int code) {
        switch(code) {
            case 0:
                return IMAGE;
            default:
            case 1:
                return DESCRIPTION;
        }
    }

    @TypeConverter
    public static int toIntCode(CombinationDescriptionType combinationDescriptionType) {
        return combinationDescriptionType.getCode();
    }}
