package com.workout.sallyapp.controller.retrofit.mock;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.workout.sallyapp.controller.retrofit.apis.GroupAPI;
import com.workout.sallyapp.controller.retrofit.apis.UserAPI;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

/**
 * Created by Yoav on 24-Apr-17.
 */

public class MockRetrofitClient {
    private static final String BASE_URL = "https://do.com/sally/"; // TODO: Change base URL

    private static Retrofit mRetrofit = null;
    private static MockRetrofit mMockRetrofit = null;

    public static MockRetrofit get() {
        if (mMockRetrofit == null) {

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .excludeFieldsWithoutExposeAnnotation()
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
                    .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        @Override
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong());
                        }
                    })
                    .create();

            // Define the interceptor, add authentication headers
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {

                    // Check if create user
                    if (chain.request().method().equals("POST") &&
                            chain.request().url().toString().equals(BASE_URL + "users/")) { // TODO: Find more elegant way to exclude just this path
                        return chain.proceed(chain.request());
                    }
                    // All other requests
                    else {
                        Request newRequest = chain.request().newBuilder().addHeader("Authorization", "1234567890").build(); //TODO: Get header at create user and save on secure shared pref
                        return chain.proceed(newRequest);
                    }
                }
            };

            // Add interceptor
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            // Retrofit client
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

            mMockRetrofit = new MockRetrofit.Builder(mRetrofit)
                    .networkBehavior(NetworkBehavior.create())
                    .build();
        }

        return mMockRetrofit;
    }

    public static MockUser createUserService() {
        BehaviorDelegate<UserAPI> delegate = MockRetrofitClient.get().create(UserAPI.class);
        return new MockUser(delegate);
    }

    public static MockGroup createGroupService() {
        BehaviorDelegate<GroupAPI> delegate = MockRetrofitClient.get().create(GroupAPI.class);
        return new MockGroup(delegate);
    }



}
