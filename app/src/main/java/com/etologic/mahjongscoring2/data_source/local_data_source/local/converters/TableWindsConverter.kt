package com.etologic.mahjongscoring2.data_source.local_data_source.local.converters

import androidx.room.TypeConverter
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*

internal class TableWindsConverter {
    
    companion object {
        
        @TypeConverter
        @JvmStatic
        fun toStatus(status: Int): TableWinds {
            return if (status == EAST.code) {
                EAST
            } else if (status == SOUTH.code) {
                SOUTH
            } else if (status == WEST.code) {
                WEST
            } else if (status == NORTH.code) {
                NORTH
            } else {
                NONE
            }
        }
        
        @TypeConverter
        @JvmStatic
        fun toInteger(tableWinds: TableWinds): Int {
            return tableWinds.code
        }
    }
}
