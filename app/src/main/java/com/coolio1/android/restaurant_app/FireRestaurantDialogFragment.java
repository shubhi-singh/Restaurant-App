package com.coolio1.android.restaurant_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.coolio1.android.restaurant_app.Example;
import com.coolio1.android.restaurant_app.Restaurant;
import com.coolio1.android.restaurant_app.RestaurantApi;
import com.coolio1.android.restaurant_app.RestaurantService;
import com.coolio1.android.restaurant_app.LocationSearchService;
import com.coolio1.android.restaurant_app.CuisineService;
import com.coolio1.android.restaurant_app.LocationSearch;
import com.coolio1.android.restaurant_app.LocationSuggestion;
import com.coolio1.android.restaurant_app.CityCuisine;
import com.coolio1.android.restaurant_app.Cuisine;
/**
 * Created by coolio1 on 4/4/18.
 */

public class FireRestaurantDialogFragment extends DialogFragment {
    private EditText locationQueryEditText;
    private EditText restaurantQueryEditText;
    private Button addLocationFilter;
    private Button addCuisineFilter;
    private Button makeSearch;
    private Button clearFilters;
    MultiSelectionSpinner spinner;
    private RestaurantService movieService;
    private LocationSearchService locationSearchService;
    private CuisineService cuisineService;
    public static String searchKeyWord = null;
    public static String selectedCuisines = null;
    private  static  int cityId = 0;
    private Button getAddLocationFilter;
    private ProgressBar progressBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("CREated", "HII");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View formView = inflater.inflate(R.layout.form_input, null);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        locationQueryEditText = (EditText) formView.findViewById(R.id.location_search_query);
        spinner = (MultiSelectionSpinner) formView.findViewById(R.id.multiSelectionSpinner);
        addLocationFilter = (Button) formView.findViewById(R.id.addLocationFilter);
        progressBar = (ProgressBar) formView.findViewById(R.id.locationLoadProgress);
        progressBar.setVisibility(View.INVISIBLE);
        
        restaurantQueryEditText = (EditText) formView.findViewById(R.id.restaurant_search_query);
        cityId = 0;
        locationQueryEditText.setText("");
        restaurantQueryEditText.setText("");

        builder.setView(formView)
                .setPositiveButton("Go!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        handleUserQuery();
                        saveSelectedCuisines();
                    }
                });

        addLocationFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocationFilter.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if(addLocationFilter.getText().toString() == "Remove"){
                    addLocationFilter.setText("Add Filter");
                    addLocationFilter.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    locationQueryEditText.setText("");
                    locationQueryEditText.setEnabled(true);
                    cityId = 0;

                }
                else {

                    callRestaurantSearchApi(locationQueryEditText.getText().toString()).enqueue(new Callback<LocationSearch>() {
                        @Override
                        public void onResponse(Call<LocationSearch> call, Response<LocationSearch> response) {
                            progressBar.setVisibility(View.INVISIBLE);
                            addLocationFilter.setVisibility(View.VISIBLE);
                            // Got data. Send it to adapter
                            if (response.equals(null)) {
                                locationQueryEditText.setText("");
                                Toast.makeText(getActivity(), getResources().getString(R.string.error_retrieving_city), Toast.LENGTH_SHORT).show();
                            }
                            else {

                                List<LocationSuggestion> results = response.body().getLocationSuggestions();

                                if (results.size() != 0) {

                                    cityId = results.get(0).getId();

                                    locationQueryEditText.setText(results.get(0).getName());
                                    Toast.makeText(getActivity(), results.get(0).getName() + " " + "has been selected!", Toast.LENGTH_SHORT).show();
                                    addLocationFilter.setText("Remove");
                                    locationQueryEditText.setEnabled(false);


                                } else {
                                    locationQueryEditText.setText("");
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_retrieving_city), Toast.LENGTH_SHORT).show();

                                }
                            }


                        }


                        @Override
                        public void onFailure(Call<LocationSearch> call, Throwable t) {
                            t.printStackTrace();
                            locationQueryEditText.setText("");
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_retrieving_city), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        getApiService();
        //FetchCuisinesFromServer
        loadCuisine();
        return builder.create();
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof MyDialogCloseListener) {
            Log.d("InputQueries", " " + searchKeyWord + " " + cityId + " " + selectedCuisines);
            ((MyDialogCloseListener) activity).handleDialogClose(dialog, new UserQuery(searchKeyWord, selectedCuisines, cityId));
        }

    }

    public void getApiService()
    {
        movieService = MainActivity.movieService;
        locationSearchService = MainActivity.locationSearchService;
        cuisineService = MainActivity.cuisineService;
    }

    public void getMembers()
    {
        searchKeyWord = MainActivity.searchKeyWord;
        selectedCuisines = MainActivity.selectedCuisines;
        cityId = MainActivity.cityId;
    }

    private Call<LocationSearch> callRestaurantSearchApi(String query) {

        return locationSearchService.getCityQuery(query);

    }
    public void loadCuisine()
    {
        callCuisineApi().enqueue(new Callback<CityCuisine>() {
            @Override
            public void onResponse(Call<CityCuisine> call, Response<CityCuisine> response) {
                // Got data. Send it to adapter
                if(response.equals(null)) Log.d("RESPONSEISNULL", "YES");
                else Log.d("RESPONSEISNULL", "no");

                List<Cuisine> results = response.body().getCuisines();
                List<String> cuisineName = new ArrayList<>();
                List<Integer> cuisineId = new ArrayList<>();

                for(Cuisine cuisine: results)
                {

                    cuisineName.add(cuisine.getCuisine().getCuisineName());
                    cuisineId.add(cuisine.getCuisine().getCuisineId());
                }

                spinner.setItems(cuisineName, cuisineId);

            }

            @Override
            public void onFailure(Call<CityCuisine> call, Throwable t) {
                t.printStackTrace();

            }
        });


    }

    private Call<CityCuisine> callCuisineApi() {

        Log.d("QUERY", searchKeyWord + "  QUERY");
        return cuisineService.getCuisineByCity(280);

    }
    //POJO class for User Data recived from the dialog fragment
    public class UserEvent {
        public final int userId;

        public UserEvent(int userId) {
            this.userId = userId;
        }
    }
    //Event Buses to regsiter closing of the dialog fragment
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(UserEvent event) {
        // Do something with userId
        Toast.makeText(getActivity(), event.userId, Toast.LENGTH_SHORT).show();
    }

    public void saveSelectedCuisines()
    {
        selectedCuisines = spinner.getSelectedItemsAsString();
    }

    public void handleUserQuery()
    {
       searchKeyWord = restaurantQueryEditText.getText().toString();
    }


    public class UserQuery
    {
        String mRestaurantSearchQuery;
        String mSelectedCuisines;
        int mCityId;

        public UserQuery(String restaurantSearchQuery, String selectedCuisines, int cityId)
        {
            mRestaurantSearchQuery = restaurantSearchQuery;
            mSelectedCuisines = selectedCuisines;
            mCityId = cityId;
        }
    }

}