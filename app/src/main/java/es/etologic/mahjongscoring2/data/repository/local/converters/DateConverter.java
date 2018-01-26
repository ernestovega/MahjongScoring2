package es.etologic.mahjongscoring2.data.repository.local.converters;

import android.arch.persistence.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateConverter {

    private static SimpleDateFormat englihSimpleDateFormat =
            new SimpleDateFormat("EEEE MM/dd/yyyy hh:mm", Locale.ENGLISH);

    @TypeConverter
    public static String toFormattedDateTime(Long timestamp) {
        return timestamp == null ? null : englihSimpleDateFormat.format(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(String formattedDateTime) throws ParseException {
        return formattedDateTime == null ? null :
                englihSimpleDateFormat.parse(formattedDateTime).getTime();
    }
}