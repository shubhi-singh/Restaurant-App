package com.coolio1.android.restaurant_app;

/**
 * Created by coolio1 on 2/4/18.
 *
 */
import com.coolio1.android.restaurant_app.Example;
import com.coolio1.android.restaurant_app.Restaurant;
import com.coolio1.android.restaurant_app.RestaurantApi;
import com.coolio1.android.restaurant_app.RestaurantService;
import com.coolio1.android.restaurant_app.LocationSearchService;
import com.coolio1.android.restaurant_app.CuisineService;
import com.coolio1.android.restaurant_app.LocationSearch;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import java.util.List;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private List<Restaurant> restaurantResults;
    private Context context;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private PaginationAdapterCallback mCallback;
    private String errorMsg;


    public PaginationAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        restaurantResults = new ArrayList<>();
    }

    public List<Restaurant> getRestaurants() {
        return restaurantResults;
    }

    public void setRestaurants(List<Restaurant> restaurantResults) {
        this.restaurantResults = restaurantResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_list, parent, false);
                viewHolder = new RestaurantVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Restaurant result = restaurantResults.get(position); // Movie

        switch (getItemViewType(position)) {

            case ITEM:
                final RestaurantVH restaurantVH = (RestaurantVH) holder;
                restaurantVH.mRestaurantName.setText(result.getRestaurant().getName());
                restaurantVH.mRestaurantLink.setText(result.getRestaurant().getUrl());
                restaurantVH.mRestaurantAddress.setText(result.getRestaurant().getLocation().getAddress());
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);
                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return restaurantResults == null ? 0 : restaurantResults.size();
    }

    @Override
    public int getItemViewType(int position) {

            return (position == restaurantResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;

    }

    public void add(Restaurant r) {
        restaurantResults.add(r);
        notifyItemInserted(restaurantResults.size() - 1);
    }

    public void addAll(List<Restaurant> restaurantResults) {
        for (Restaurant result : restaurantResults) {
            add(result);
        }
    }

    public void remove(Restaurant r) {
        int position = restaurantResults.indexOf(r);
        if (position > -1) {
            restaurantResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Restaurant());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = restaurantResults.size() - 1;
        Restaurant result = getItem(position);

        if (result != null) {
            restaurantResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Restaurant getItem(int position) {
        return restaurantResults.get(position);
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(restaurantResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


    protected class RestaurantVH extends RecyclerView.ViewHolder {
        private TextView mRestaurantName;
        private TextView mRestaurantAddress;
        private TextView mRestaurantLink;
       

        public RestaurantVH(View itemView) {
            super(itemView);
            mRestaurantName = (TextView) itemView.findViewById(R.id.restaurant_name_tv);
            mRestaurantAddress = (TextView) itemView.findViewById(R.id.restaurant_address_tv);
            mRestaurantLink = (TextView) itemView.findViewById(R.id.restaurant_link_tv);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

}
