package com.workout.sallyapp.view.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Yoav on 13-Apr-17.
 */

public class XAxisDateFormatter implements IAxisValueFormatter {

    public static final int DIVIDE_BY = 1000;

    private final java.text.DateFormat dateFormat;
    private float minValue;

    public XAxisDateFormatter() {
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return dateFormat.format(getDatefromValue((long) (value + minValue)));
    }

    public Date getDatefromValue(long value) {
        return new Date(value * DIVIDE_BY);
    }

    public long getValuefromDate(Date date) {
        return date.getTime() / DIVIDE_BY;
    }
}
