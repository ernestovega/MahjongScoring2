package es.etologic.mahjongscoring2.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    private static final String DATE_FORMAT = "EEEE MM/dd/yyyy HH:mm";
    private static final String TIME_FORMAT = "HH:mm:ss";

    public static String getPrettyDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String getPrettyTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        return sdf.format(time);
    }

    public static boolean areEqual(Date date1, Date date2) {
        if(date1 == null && date2 == null) return true;
        //noinspection SimplifiableIfStatement
        if(date1 == null || date2 == null) return false;
        return date1 == date2;
    }
}
