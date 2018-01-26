package es.etologic.mahjongscoring2.utils;

import android.text.format.DateFormat;

import java.util.Date;

public class DateUtils {

    private static final String YYYY_MMMM_DD = "yyyy MMMM dd";

    public static String getPrettyDate(Date date) {
        if(date == null) return "";
        return DateFormat.format(YYYY_MMMM_DD, date).toString();
    }
}
