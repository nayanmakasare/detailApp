package fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                        for(MovieTile movieTile : movieRow.getRowItems()) {
                            movieTile.setRowLayout(movieRow.getRowLayout());
                            arrayObjectAdapter.add(movieTile);
                        }
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

    private void setupUIElements() {
        setTitle("Cloudwalker");
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
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
}
