package es.etologic.mahjongscoring2.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.etologic.mahjongscoring2.domain.model.Round;

public class DateTimeUtils {

    //CONSTANTS
    private static final long HOUR_MILLIS = 1000*60*60;
    private static final long HOUR_MINUTES = 60;
    private static final String DATE_FORMAT = "EEEE d MMMM yyyy HH:mm";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String NO_DURATION = "-";

    public static String getPrettyDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String getPrettyTime(long time) {
        if (time <= 0) {
            return NO_DURATION;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
            return sdf.format(time);
        }
    }

    public static String getPrettyDuration(List<Round> rounds) {
        if (rounds != null) {
            long duration = 0;
            for (Round round : rounds) {
                duration += round.getRoundDuration();
            }
            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - (hours*HOUR_MINUTES);
            return String.format(Locale.getDefault(), "%2dh %2dm", hours, minutes);
        } else {
            return "-";
        }
    }

    public static boolean areEqual(Date date1, Date date2) {
        if (date1 == null && date2 == null) return true;
        //noinspection SimplifiableIfStatement
        if (date1 == null || date2 == null) return false;
        return date1 == date2;
    }
}
