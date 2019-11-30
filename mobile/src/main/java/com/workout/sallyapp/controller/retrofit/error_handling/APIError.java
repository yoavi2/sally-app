package com.workout.sallyapp.controller.retrofit.error_handling;

import android.support.annotation.Keep;

/**
 * Created by Yoav on 09-May-17.
 */
@Keep // TODO: Check if needed
public class APIError {

    private int statusCode;
    private String message;

    public APIError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}