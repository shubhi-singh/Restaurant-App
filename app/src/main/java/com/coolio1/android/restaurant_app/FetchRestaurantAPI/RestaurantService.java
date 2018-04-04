package com.coolio1.android.restaurant_app;

/**
 * Created by coolio1 on 2/4/18.
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import com.coolio1.android.restaurant_app.Example;



public interface RestaurantService {
    @Headers("user-key: c750173e8cf7e5fdc2c331cf897ee8c3")

    @GET("search")

    Call<Example> getRestaurants(@Query("q") String query,
                                    @Query("entity_id") int id,
                                    @Query("entity_type") String type,
                                    @Query("cuisines") String selectedCuisines

    );

}
