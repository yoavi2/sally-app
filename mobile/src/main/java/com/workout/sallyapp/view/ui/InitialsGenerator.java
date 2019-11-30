package com.workout.sallyapp.view.ui;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by Yoav on 27-Apr-17.
 */
public class InitialsGenerator {

    private ColorGenerator mColorGenerator;

    public InitialsGenerator(boolean isMaterial) {
        this.mColorGenerator = isMaterial ? ColorGenerator.MATERIAL : ColorGenerator.DEFAULT;
    }

    public TextDrawable createRect(String text, Object key) {
        return TextDrawable.builder()
                .rect().build(getInitials(text),
                        mColorGenerator.getColor(key));
    }

    public TextDrawable createCircle(String text, Object key) {
        return TextDrawable.builder()
                .round().build(getInitials(text),
                        mColorGenerator.getColor(key));
    }

    public static String getInitials(String name) {
        String upperName = name.toUpperCase();
        String initials = "";
        String[] parts = upperName.split(" ");

        initials += upperName.charAt(0);

        if (parts.length >= 2) {
            initials += parts[parts.length - 1].charAt(0);
        }

        return initials;
    }

}
