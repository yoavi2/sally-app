package com.workout.sallyapp.utilities;

import java.util.Locale;

/**
 * Created by Yoav on 05-Apr-17.
 */

public class TimeUtility {

    public static String formatSeconds(int seconds) {
        return String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60);
    }
}
