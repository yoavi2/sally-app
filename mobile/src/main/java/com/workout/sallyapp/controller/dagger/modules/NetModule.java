package com.workout.sallyapp.controller.dagger.modules;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import android.app.Application;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.workout.sallyapp.BuildConfig;
import com.workout.sallyapp.controller.dagger.qualifiers.BaseUrlQualifier;
import com.workout.sallyapp.model.repository.entity_repositories.UserRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yoav on 19-Jul-17.
 */
@Module
public class NetModule {

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaredClass().equals(ModelAdapter.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.getTime());
                    }
                })
                .create();
    }

    @Provides
    @Singleton
    Cache provideHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(final Application application, Cache cache, final UserRepository userRepository, @BaseUrlQualifier final String baseUrl) {
        // Define the interceptor, add authentication headers
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                // Check if not create user. if So try to attach Authorization header
                if (!(chain.request().method().equals("POST") &&
                        chain.request().url().toString().equals(baseUrl + "users/"))) { // TODO: Find more elegant way to exclude just this path
                    String auth = userRepository.getUserJWT(); // TODO: Pass this as a parameter and use UserScope to init again after login

                    if (auth != null) {
                        Request newRequest = chain.request().newBuilder().addHeader("Authorization", auth).build();
                        return chain.proceed(newRequest);
                    }
                }
                // All other requests
                return chain.proceed(chain.request());
            }
        };

        // Add interceptor + Stetho interceptor
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);
        builder.interceptors().add(interceptor);

        // Only enable stetho and logging in debug mode
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());

            // Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }

        return builder.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient, @BaseUrlQualifier final String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}
