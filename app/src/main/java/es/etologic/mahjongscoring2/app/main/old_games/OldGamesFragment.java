package es.etologic.mahjongscoring2.app.main.old_games;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.base.BaseFragment;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.model.Game;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.NEW_GAME;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class OldGamesFragment extends BaseFragment implements OldGamesRvAdapter.GameItemListener {

    //VIEWS
    @BindView(R.id.toolbarOldGames) Toolbar toolbar;
    @BindView(R.id.swipeRefreshLayoutOldGames) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerViewOldGames) RecyclerView recyclerView;
    @BindView(R.id.emptyLayoutOldGames) LinearLayout emptyLayout;
    @BindView(R.id.fabOldGames) FloatingActionButton fabNewGame;
    //FIELDS
    private Unbinder unbinder;
    private OldGamesRvAdapter rvAdapter;
    private Context context;
    @Inject MainActivityViewModelFactory mainActivityViewModelFactory;
    @Inject OldGamesViewModelFactory oldGamesViewModelFactory;
    private MainActivityViewModel activityViewModel;
    private OldGamesViewModel viewModel;

    //EVENTS
    @OnClick(R.id.fabOldGames) public void onFabNewGameClick() {
        activityViewModel.navigateTo(NEW_GAME);
    }
    @Override public void onOldGameItemDeleteClicked(long gameId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_game)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteGame(gameId))
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
    @Override public void onOldGameItemResumeClicked(long gameId) { activityViewModel.goToGame(gameId); }

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.oldgames_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        setupRecyclerView();
        setupViewModel();
        setupSwipeRefreshLayout();
        setToolbar();
    }
    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdapter = new OldGamesRvAdapter();
        rvAdapter.setOldGameItemListener(this);
        recyclerView.setAdapter(rvAdapter);
    }
    private void setupViewModel() {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            activityViewModel = ViewModelProviders.of(activity, mainActivityViewModelFactory).get(MainActivityViewModel.class);
            viewModel = ViewModelProviders.of(this, oldGamesViewModelFactory).get(OldGamesViewModel.class);
            viewModel.getError().observe(this, this::showError);
            viewModel.getGames().observe(this, this::setGames);
            viewModel.getProgressState().observe(this, this::toogleLocalProgress);
            viewModel.getSnackbarMessage().observe(this, this::showSnackbar);
        }
    }
    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getAllGames());
    }
    private void setToolbar() { activityViewModel.setToolbar(toolbar); }
    private void setGames(List<Game> games) {
        if(games == null || games.isEmpty()) {
            emptyLayout.setVisibility(VISIBLE);
            fabNewGame.setVisibility(VISIBLE);
        } else {
            emptyLayout.setVisibility(GONE);
            fabNewGame.setVisibility(GONE);
            rvAdapter.setGames(games);
        }
        toogleLocalProgress(HIDE);
    }
    private void toogleLocalProgress(ShowState showState) {
        swipeRefreshLayout.setRefreshing(showState == SHOW);
    }
    private void showSnackbar(String message) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG).show();
    }
    @Override
    public void onResume() {
        super.onResume();
        viewModel.getAllGames();
    }
    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
