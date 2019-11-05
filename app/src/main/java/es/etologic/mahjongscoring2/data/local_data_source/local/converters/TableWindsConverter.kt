package es.etologic.mahjongscoring2.data.local_data_source.local.converters

import androidx.room.TypeConverter
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*

object TableWindsConverter {
    
    @TypeConverter
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
    fun toInteger(tableWinds: TableWinds): Int {
        return tableWinds.code
    }
}
