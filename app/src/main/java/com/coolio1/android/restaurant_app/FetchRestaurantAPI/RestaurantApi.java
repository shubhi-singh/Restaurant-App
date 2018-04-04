package com.coolio1.android.restaurant_app;

/**
 * Created by coolio1 on 2/4/18.
 */
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantApi {

    private static Retrofit retrofit = null;

        private static OkHttpClient buildClient() {
            return new OkHttpClient
                    .Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();
        }

        public static Retrofit getClient() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .client(buildClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://developers.zomato.com/api/v2.1/")
                        .build();
            }
            return retrofit;
        }



}
