package com.coolio1.android.restaurant_app;

import com.coolio1.android.restaurant_app.Example;
import com.coolio1.android.restaurant_app.Restaurant;
import com.coolio1.android.restaurant_app.RestaurantApi;
import com.coolio1.android.restaurant_app.RestaurantService;
import com.coolio1.android.restaurant_app.LocationSearchService;
import com.coolio1.android.restaurant_app.CuisineService;
import com.coolio1.android.restaurant_app.LocationSearch;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback, MyDialogCloseListener {

    private static final String TAG = "MainActivity";
    public static PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;
    Toolbar toolbar;
    private static final int PAGE_START = 1;
    public static String searchKeyWord = null;
    public static String selectedCuisines = null;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 25;
    private int currentPage = PAGE_START;
    public static int cityId = 0;
    public static RestaurantService movieService;
    public static LocationSearchService locationSearchService;
    public static CuisineService cuisineService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        btnRetry = (Button) findViewById(R.id.error_btn_retry);
        txtError = (TextView) findViewById(R.id.error_txt_cause);

        adapter = new PaginationAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //initialise service and load data
        movieService = RestaurantApi.getClient().create(RestaurantService.class);
        locationSearchService = RestaurantApi.getClient().create(LocationSearchService.class);
        cuisineService = RestaurantApi.getClient().create(CuisineService.class);

        loadFirstPage();
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });

    }

    @Override
    public void handleDialogClose(DialogInterface dialog, FireRestaurantDialogFragment.UserQuery query) {
        searchKeyWord = query.mRestaurantSearchQuery;
        selectedCuisines = query.mSelectedCuisines;
        cityId = query.mCityId;
        loadFirstPage();
        adapter.notifyDataSetChanged();
        Log.d("INPUTQUERIES", searchKeyWord+" "+cityId+" "+selectedCuisines);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.searchInMenu)

            startRestaurantSearchDialog();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUMED MAINACTIVITY","YES");
        super.onPostResume();
        loadFirstPage();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadFirstPage();
        adapter.notifyDataSetChanged();
    }

    public  void loadFirstPage() {

        hideErrorView();
        adapter.clear();

        callRestaurantsApi().enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                // Got data. Send it to adapter
                if(response.equals(null)) invalidQuery();
                else Log.d("RESPONSEISNULL", "no");
                hideErrorView();

                List<Restaurant> results = fetchRestaurants(response);
                if(results.size() != 0) {
                    progressBar.setVisibility(View.GONE);
                    adapter.addAll(results);
                    if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                }
                else {
                  invalidQuery();}
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }


    public void startRestaurantSearchDialog(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FireRestaurantDialogFragment formDialog = new FireRestaurantDialogFragment();
        resetSearchFields();
        formDialog.show(fragmentManager, "FORM");
    }

    private List<Restaurant> fetchRestaurants(Response<Example> response) {
        Example topRatedMovies = response.body();
        return topRatedMovies.getRestaurants();
    }

    private void loadNextPage() {

        callRestaurantsApi().enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                List<Restaurant> results = fetchRestaurants(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    private Call<Example> callRestaurantsApi() {

        return movieService.getRestaurants(
                searchKeyWord,
                cityId,"city",
                selectedCuisines

               );
    }

    private Call<LocationSearch> callRestaurantSearchApi(String query) {

        return locationSearchService.getCityQuery(query);

    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            txtError.setText(fetchErrorMessage(throwable));
        }
    }


    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    private void hideErrorView() {

        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void resetSearchFields() {

        selectedCuisines = null;
        cityId = 0;
        searchKeyWord = null;
    }

    public void invalidQuery()
    {

        Toast.makeText(MainActivity.this, "Invalid query! Select a different cuisine, city or restaurant", Toast.LENGTH_LONG).show();
        selectedCuisines = null;
        searchKeyWord = null;
        cityId = 0;
        loadFirstPage();
        adapter.notifyDataSetChanged();

    }
}
