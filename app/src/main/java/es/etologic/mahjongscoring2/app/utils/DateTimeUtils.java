package es.etologic.mahjongscoring2.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    private static final String DATE_FORMAT = "EEEE MM/dd/yyyy HH:mm";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String NO_DURATION = "-";

    public static String getPrettyDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String getPrettyTime(long time) {
        if(time <= 0) {
            return NO_DURATION;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
            return sdf.format(time);
        }
    }

    public static boolean areEqual(Date date1, Date date2) {
        if(date1 == null && date2 == null) return true;
        //noinspection SimplifiableIfStatement
        if(date1 == null || date2 == null) return false;
        return date1 == date2;
    }
    private static final long AN_HOUR_IN_MILLIS = 1000*60*60;
}
