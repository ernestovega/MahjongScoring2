package es.etologic.mahjongscoring2.app.main.old_games;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class OldGamesFragment extends BaseFragment implements OldGamesRvAdapter.GameItemListener {

    //VIEWS
    @BindView(R.id.toolbarOldGames) Toolbar toolbar;
    @BindView(R.id.swipeRefreshLayoutOldGames) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerViewOldGames) RecyclerView recyclerView;
    @BindView(R.id.emptyLayoutOldGames) LinearLayout emptyLayout;
    //FIELDS
    private Unbinder unbinder;
    private OldGamesRvAdapter rvAdapter;
    @Inject MainActivityViewModelFactory mainActivityViewModelFactory;
    private MainActivityViewModel activityViewModel;
    @Inject OldGamesViewModelFactory oldGamesViewModelFactory;
    private OldGamesViewModel viewModel;

    //EVENTS
    @OnClick(R.id.fabOldGames) public void onFabNewGameClick() {
        activityViewModel.startGame(null);
    }
    @Override public void onOldGameItemDeleteClicked(long gameId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_game)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteGame(gameId))
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
    @Override public void onOldGameItemResumeClicked(long gameId) {
        activityViewModel.startGame(gameId);
    }

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.oldgames_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        if (getActivity() != null) {
            activityViewModel = ViewModelProviders.of(getActivity(), mainActivityViewModelFactory).get(MainActivityViewModel.class);
            viewModel = ViewModelProviders.of(this, oldGamesViewModelFactory).get(OldGamesViewModel.class);
            viewModel.getError().observe(this, this::showError);
            viewModel.getGames().observe(this, this::setGames);
            viewModel.getLocalProgressState().observe(this, this::toogleLocalProgress);
            viewModel.getSnackbarMessage().observe(this, this::showSnackbar);
        }
    }
    private void setGames(List<GameWithRounds> games) {
        if (games == null || games.isEmpty()) {
            emptyLayout.setVisibility(VISIBLE);
        } else {
            emptyLayout.setVisibility(GONE);
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
    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getAllGames());
    }
    private void setToolbar() { activityViewModel.setToolbar(toolbar); }
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
