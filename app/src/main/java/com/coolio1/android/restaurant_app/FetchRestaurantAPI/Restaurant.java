
package com.coolio1.android.restaurant_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.coolio1.android.restaurant_app.Restaurant_;

public class Restaurant {

    @SerializedName("restaurant")
    @Expose
    private Restaurant_ restaurant;

    public Restaurant_ getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant_ restaurant) {
        this.restaurant = restaurant;
    }

}
