package com.coolio1.android.restaurant_app;

import android.content.DialogInterface;

/**
 * Created by coolio1 on 4/4/18.
 */

public interface MyDialogCloseListener
{
    public void handleDialogClose(DialogInterface dialog, FireRestaurantDialogFragment.UserQuery query);//or whatever args you want
}