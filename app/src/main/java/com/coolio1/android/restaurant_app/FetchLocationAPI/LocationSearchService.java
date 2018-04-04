package com.coolio1.android.restaurant_app;


/**
 * Created by coolio1 on 2/4/18.
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import com.coolio1.android.restaurant_app.LocationSearch;


public interface LocationSearchService {
    @Headers("user-key: c750173e8cf7e5fdc2c331cf897ee8c3")

    @GET("cities")

    Call<LocationSearch> getCityQuery(@Query("q") String query

    );

}
