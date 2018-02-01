package es.etologic.mahjongscoring2.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String SIMPLE_DATE_FORMAT = "EEEE MM/dd/yyyy HH:mm";

    public static String getPrettyDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.ENGLISH);
        return sdf.format(date);
    }

    public static boolean areEqual(Date date1, Date date2) {
        if(date1 == null && date2 == null) return true;
        //noinspection SimplifiableIfStatement
        if(date1 == null || date2 == null) return false;
        return date1 == date2;
    }
}
