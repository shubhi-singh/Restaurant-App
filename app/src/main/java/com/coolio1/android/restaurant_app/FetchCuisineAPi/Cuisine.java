
package com.coolio1.android.restaurant_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.coolio1.android.restaurant_app.Cuisine_;

public class Cuisine {

    @SerializedName("cuisine")
    @Expose
    private Cuisine_ cuisine;

    public Cuisine_ getCuisine() {
        return cuisine;
    }

    public void setCuisine(Cuisine_ cuisine) {
        this.cuisine = cuisine;
    }

}
