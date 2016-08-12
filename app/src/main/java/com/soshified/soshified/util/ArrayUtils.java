package com.soshified.soshified.util;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods pertaining to Arrays
 */

public class ArrayUtils
{
    public static <C> List<C> asList(SparseArray<C> sparseArray)
    {
        if (sparseArray == null)
            return new ArrayList<C>();

        List<C> arrayList = new ArrayList<>(sparseArray.size());

        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));

        return arrayList;
    }

}
