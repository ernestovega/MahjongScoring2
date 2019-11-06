package es.etologic.mahjongscoring2.data.local_data_source.local.converters

import androidx.room.TypeConverter
import java.util.*

internal class DateConverter {
    
    internal companion object {
        
        @TypeConverter
        @JvmStatic
        internal fun toDate(timestamp: Long?): Date? {
            return if (timestamp == null) null else Date(timestamp)
        }
        
        @TypeConverter
        @JvmStatic
        internal fun toTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}