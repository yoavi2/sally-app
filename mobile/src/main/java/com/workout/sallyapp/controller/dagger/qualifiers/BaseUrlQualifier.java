package com.workout.sallyapp.controller.dagger.qualifiers;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Yoav on 01-Aug-17.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface BaseUrlQualifier {
}
