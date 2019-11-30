package com.workout.sallyapp.view.ui; /**
 +     * Behavior designed for use with {@link FloatingActionMenu} instances. Its main function
 +     * is to move {@link FloatingActionMenu} views so that any displayed {@link Snackbar}s do
 +     * not cover them.
 +     */

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

/**
 * Behavior designed for use with {@link FloatingActionMenu} instances. Its main function
 * is to move {@link FloatingActionMenu} views so that any displayed {@link Snackbar}s do
 * not cover them.
 */
public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {
    public FloatingActionMenuBehavior(){
        super();
    }

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        float translationY = Math.min(0, ViewCompat.getTranslationY(dependency) - dependency.getHeight());
        ViewCompat.setTranslationY(child, translationY);
        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        ViewCompat.animate(child).translationY(0).start();

    }
}