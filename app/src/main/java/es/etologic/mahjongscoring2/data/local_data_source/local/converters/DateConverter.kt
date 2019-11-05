package es.etologic.mahjongscoring2.data.local_data_source.local.converters

import androidx.room.TypeConverter
import java.util.*

object DateConverter {
    
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }
    
    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}