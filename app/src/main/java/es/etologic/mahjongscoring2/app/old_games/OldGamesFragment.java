package es.etologic.mahjongscoring2.app.old_games;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.OldGame;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class OldGamesFragment extends Fragment implements OldGamesRvAdapter.OldGameItemListener {

    //region Fields

    @BindView (R.id.slOldGames) SwipeRefreshLayout swipeLayout;
    @BindView (R.id.rvOldGames) RecyclerView rvOldGames;
    @BindView (R.id.llOldGamesEmptyView) LinearLayout emptyLayout;
    private Unbinder unbinder;
    private OldGamesRvAdapter rvAdapter;
    private Context context;
    private OldGamesViewModel viewModel;

    //endregion

    //region Lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.oldgames_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        setupViewModel();
        setupSwipeRefresh();
        setupRecyclerView();
        observeViewModel();
        viewModel.loadOldGames();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    //endregion

    //region Events

    @OnClick (R.id.fabOldGamesNewGame)
    public void onFabNewGameClick() {
        Snackbar.make(swipeLayout, "fabOldGamesNewGame clicked!", Snackbar.LENGTH_LONG)
                .show();
    }

    //endregion

    //region OldGameItemListener

    @Override
    public void onOldGameItemDeleteClicked(int gameId) {
        String message = String.format(Locale.getDefault(),"game %d delete clicked!", gameId);
        Snackbar.make(swipeLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onOldGameItemResumeClicked(int gameId) {
        String message = String.format(Locale.getDefault(),"game %d resume clicked!", gameId);
        Snackbar.make(swipeLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }

    //endregion

    //region Private

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideOldGamesViewModelFactory(context))
                .get(OldGamesViewModel.class);
    }

    private void setupSwipeRefresh() {
        swipeLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        swipeLayout.setOnRefreshListener(() -> viewModel.loadOldGames());
    }

    public void setupRecyclerView() {
        rvOldGames.setHasFixedSize(true);
        rvOldGames.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdapter = new OldGamesRvAdapter();
        rvAdapter.setOldGameItemListener(this);
        rvOldGames.setAdapter(rvAdapter);
    }

    private void observeViewModel() {
        viewModel.getOldGames().observe(this, this :: setOldGames);
        viewModel.getProgressState().observe(this, this :: toogleLocalProgress);
        viewModel.getSnackbarMessage().observe(this, this :: showSnackbar);
    }

    private void setOldGames(List<OldGame> oldGames) {
        rvAdapter.setOldGames(oldGames);
        toogleLocalProgress(HIDE);
    }

    private void toogleLocalProgress(ShowState showState) {
        swipeLayout.setRefreshing(showState == SHOW);
    }

    private void showSnackbar(String message) {
        Snackbar.make(swipeLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }

    //endregion
}
