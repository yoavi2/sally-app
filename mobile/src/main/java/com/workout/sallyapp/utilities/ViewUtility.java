package com.workout.sallyapp.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Yoav on 26-Apr-17.
 */

public class ViewUtility {

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
