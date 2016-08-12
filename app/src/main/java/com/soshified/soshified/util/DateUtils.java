package com.soshified.soshified.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for working with Dates
 */
public class DateUtils
{
    public DateUtils() {}

    public static String parseWordPressFormat(long timestamp)
    {
        return (new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)).format(new Date(timestamp));
    }

    public static long getUnixTimeStamp(String date)
    {
        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try
        {
            return fromFormat.parse(date).getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return 0;
        }
    }
}
