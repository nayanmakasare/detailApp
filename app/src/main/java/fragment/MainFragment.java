package fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Timer;
import java.util.TimerTask;

import api.ApiClient;
import api.ApiInterface;
import model.MovieResponse;
import model.MovieRow;
import model.MovieTile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.cloudwalker.detailapp.BrowseErrorActivity;
import tv.cloudwalker.detailapp.CardPresenter;
import tv.cloudwalker.detailapp.ErrorFragment;
import tv.cloudwalker.detailapp.R;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;

//    private final Handler mHandler = new Handler();
//    private Drawable mDefaultBackground;
//    private DisplayMetrics mMetrics;
//    private Timer mBackgroundTimer;
//    private String mBackgroundUri;
//    private BackgroundManager mBackgroundManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (null != mBackgroundTimer) {
//            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
//            mBackgroundTimer.cancel();
//        }
    }

    private void loadRows() {

        ListRowPresenter listRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_XSMALL, false);
        listRowPresenter.enableChildRoundedCorners(true);
        listRowPresenter.setKeepChildForeground(true);
        listRowPresenter.setShadowEnabled(false);
        listRowPresenter.setSelectEffectEnabled(false);
        final ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(listRowPresenter);

        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<MovieResponse> call = apiService.getHomeScreenData();
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response)
            {
                if(response.code() ==200 && response.body() != null){
                    MovieResponse movieResponse = response.body();
                    for(MovieRow movieRow : movieResponse.getRows())
                    {
                        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new CardPresenter());
                        arrayObjectAdapter.addAll(0,movieRow.getRowItems());
                        ListRow listRow = new ListRow(movieRow.getRowIndex(), new HeaderItem(movieRow.getRowHeader()), arrayObjectAdapter);
                        rowsAdapter.add(listRow);
                    }

                } else if (response.code() == 401) {
                    getFragmentManager().beginTransaction().add(new ErrorFragment(),ErrorFragment.class.getSimpleName()).commit();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ",t );
            }
        });
        setAdapter(rowsAdapter);
    }

    private void prepareBackgroundManager() {

//        mBackgroundManager = BackgroundManager.getInstance(getActivity());
//        mBackgroundManager.attach(getActivity().getWindow());
//
//        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
//        mMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle("Cloudwalker"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

//    private void updateBackground(String uri) {
//        int width = mMetrics.widthPixels;
//        int height = mMetrics.heightPixels;
//        Glide.with(getActivity())
//                .load(uri)
//                .centerCrop()
//                .error(mDefaultBackground)
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        mBackgroundManager.setDrawable(resource);
//                    }
//                });
//        mBackgroundTimer.cancel();
//    }

    private void startBackgroundTimer() {
//        if (null != mBackgroundTimer) {
//            mBackgroundTimer.cancel();
//        }
//        mBackgroundTimer = new Timer();
//        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof MovieTile) {
                MovieTile movie = (MovieTile) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cloudwalker://launcher.detail/"+movie.getTid()));
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        "hero")
                        .toBundle();
                bundle.putParcelable(MovieTile.class.getSimpleName(), movie);
                intent.putExtra(MovieTile.class.getSimpleName(), bundle);
                getActivity().startActivity(intent);
            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
//            if (item instanceof MovieTile) {
//                mBackgroundUri = ((MovieTile) item).getBackground();
//                startBackgroundTimer();
//            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    updateBackground(mBackgroundUri);
//                }
//            });
        }
    }
}
