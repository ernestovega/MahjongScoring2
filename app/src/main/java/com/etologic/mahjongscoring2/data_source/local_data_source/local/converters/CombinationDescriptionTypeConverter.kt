package com.etologic.mahjongscoring2.data_source.local_data_source.local.converters

import androidx.room.TypeConverter
import com.etologic.mahjongscoring2.business.model.entities.Combination.CombinationDescriptionType
import com.etologic.mahjongscoring2.business.model.entities.Combination.CombinationDescriptionType.DESCRIPTION
import com.etologic.mahjongscoring2.business.model.entities.Combination.CombinationDescriptionType.IMAGE

class CombinationDescriptionTypeConverter {
    
    companion object {
        
        @TypeConverter
        @JvmStatic
        fun toCombinationDescriptionType(code: Int): CombinationDescriptionType {
            return when (code) {
                0 -> IMAGE
                else -> DESCRIPTION
            }
        }
        
        @TypeConverter
        @JvmStatic
        fun toIntCode(combinationDescriptionType: CombinationDescriptionType): Int {
            return combinationDescriptionType.code
        }
        
    }
}
