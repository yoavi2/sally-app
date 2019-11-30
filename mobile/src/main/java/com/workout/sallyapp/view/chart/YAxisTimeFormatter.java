package com.workout.sallyapp.view.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.workout.sallyapp.utilities.TimeUtility;

/**
 * Created by Yoav on 13-Apr-17.
 */

public class YAxisTimeFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return TimeUtility.formatSeconds((int) value);
    }
}
