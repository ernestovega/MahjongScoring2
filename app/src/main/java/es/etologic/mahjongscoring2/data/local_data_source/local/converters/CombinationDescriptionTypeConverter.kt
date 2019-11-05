package es.etologic.mahjongscoring2.data.local_data_source.local.converters

import androidx.room.TypeConverter
import es.etologic.mahjongscoring2.domain.model.Combination.CombinationDescriptionType
import es.etologic.mahjongscoring2.domain.model.Combination.CombinationDescriptionType.DESCRIPTION
import es.etologic.mahjongscoring2.domain.model.Combination.CombinationDescriptionType.IMAGE

object CombinationDescriptionTypeConverter {
    
    @TypeConverter
    fun toCombinationDescriptionType(code: Int): CombinationDescriptionType {
        return when (code) {
            0 -> IMAGE
            else -> DESCRIPTION
        }
    }
    
    @TypeConverter
    fun toIntCode(combinationDescriptionType: CombinationDescriptionType): Int {
        return combinationDescriptionType.code
    }
}
