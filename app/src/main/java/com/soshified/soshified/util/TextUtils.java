package com.soshified.soshified.util;

import android.content.Context;
import android.text.Html;

/**
 * Utility methods for text modification and formatting
 */
public class TextUtils {

    public TextUtils() {}

    public static String fromHtml(String originalString) {
        return Html.fromHtml(originalString).toString().trim();
    }

    public static String formatStringRes(Context context, int resId, String[] strings) {
        String mStringResource = context.getString(resId);
        return String.format(mStringResource, strings);
    }
}
