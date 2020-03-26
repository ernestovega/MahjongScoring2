package com.etologic.mahjongscoring2.data_source.local_data_source.local.converters

import androidx.room.TypeConverter
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*

internal class TableWindsConverter {
    
    companion object {
        
        @TypeConverter
        @JvmStatic
        fun toStatus(status: Int): TableWinds {
            return when (status) {
                EAST.code -> EAST
                SOUTH.code -> SOUTH
                WEST.code -> WEST
                NORTH.code -> NORTH
                else -> NONE
            }
        }
        
        @TypeConverter
        @JvmStatic
        fun toInteger(tableWinds: TableWinds): Int {
            return tableWinds.code
        }
    }
}
