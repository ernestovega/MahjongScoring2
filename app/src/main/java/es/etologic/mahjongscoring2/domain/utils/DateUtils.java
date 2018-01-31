package es.etologic.mahjongscoring2.domain.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String SIMPLE_DATE_FORMAT = "EEEE MM/dd/yyyy hh:mm";

    public static String getPrettyDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.ENGLISH);
        return sdf.format(date);
    }
}
