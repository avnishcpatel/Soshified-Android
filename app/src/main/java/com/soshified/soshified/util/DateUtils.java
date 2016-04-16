package com.soshified.soshified.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for working with Dates
 */
public class DateUtils {

    public DateUtils() {}

    public static String parseWordPressFormat(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat toFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        return toFormat.format(date);

    }

    public static long getUnixTimeStamp(String date) {
        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date parsedDate = fromFormat.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
