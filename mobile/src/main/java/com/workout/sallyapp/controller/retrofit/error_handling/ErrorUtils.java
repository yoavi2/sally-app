package com.workout.sallyapp.controller.retrofit.error_handling;

import android.support.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Yoav on 09-May-17.
 */

@Singleton
public class ErrorUtils {

    private final Converter<ResponseBody, APIError> converter;

    @Inject
    public ErrorUtils(Converter<ResponseBody, APIError> converter) {
        this.converter = converter;
    }

    public APIError parseError(@NonNull Response<?> response) {

        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }
}