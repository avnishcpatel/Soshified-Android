package com.soshified.soshified.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;

/**
 * Utility methods for text modification and formatting
 */
public class TextUtils
{
    public TextUtils() {}

    @SuppressWarnings("deprecation")
    public static String fromHtml(String originalString)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return Html.fromHtml(originalString, 0).toString().trim();
        }
        else return Html.fromHtml(originalString).toString().trim();
    }
    
    public static String formatStringRes(Context context, int resId, String[] strings)
    {
        return String.format(context.getString(resId), (Object)strings);
    }

    public static String validateImageUrl(String url)
    {
        return ( url != null && url.contains(" ") ) ? url.replaceAll(" ", "%20") : url;
    }
}
