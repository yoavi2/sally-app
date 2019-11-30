package com.workout.sallyapp.controller.dagger.modules;

import com.workout.sallyapp.controller.retrofit.error_handling.APIError;

import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Yoav on 20-Jul-17.
 */

@Module
public class ErrorUtilsModule {

    @Provides
    @Singleton
    public Converter<ResponseBody, APIError> getRetrofitErrorConverter(Retrofit retrofit) {
        return retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
    }
}
