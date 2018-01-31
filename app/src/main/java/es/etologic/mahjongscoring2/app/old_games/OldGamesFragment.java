package es.etologic.mahjongscoring2.app.old_games;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import es.etologic.mahjongscoring2.app.game.GameActivity;
import es.etologic.mahjongscoring2.app.main.IMainToolbarListener;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.Game;

import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class OldGamesFragment extends Fragment implements OldGamesRvAdapter.GameItemListener {

    //region Interfaz

    public interface IOldGamesFragmentListener extends IMainToolbarListener {
        void goToNewGame();
    }

    //endregion

    //region Fields

    @BindView (R.id.toolbarOldGames) Toolbar toolbar;
    @BindView (R.id.swipeRefreshLayoutOldGames) SwipeRefreshLayout swipeRefreshLayout;
    @BindView (R.id.recyclerViewOldGames) RecyclerView recyclerView;
    @BindView (R.id.emptyLayoutOldGames) LinearLayout emptyLayout;
    private Unbinder unbinder;
    private OldGamesRvAdapter rvAdapter;
    private Context context;
    private OldGamesViewModel viewModel;
    private IOldGamesFragmentListener oldGamesFragmentListener;

    //endregion

    //region Lifecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.oldgames_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        setupViewModel();
        setupSwipeRefreshLayout();
        setupRecyclerView();
        observeViewModel();
        viewModel.loadGames();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(oldGamesFragmentListener != null && toolbar != null) {
            oldGamesFragmentListener.setToolbar(toolbar);
        }
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    //endregion

    //region Public

    public void setOldGamesFragmentListener(IOldGamesFragmentListener oldGamesFragmentListener) {
        this.oldGamesFragmentListener = oldGamesFragmentListener;
    }

    //endregion

    //region Events

    @OnClick (R.id.fabOldGames)
    public void onFabNewGameClick() {
        if(oldGamesFragmentListener != null) {
            oldGamesFragmentListener.goToNewGame();
        }
    }

    //endregion

    //region OldGameItemListener

    @Override
    public void onOldGameItemDeleteClicked(long gameId) {
        //TODO
        String message = String.format(Locale.getDefault(),"game %d delete clicked!", gameId);
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onOldGameItemResumeClicked(long gameId) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(getString(R.string.key_extra_game_id), gameId);
        startActivity(intent);
    }

    //endregion

    //region Private

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideOldGamesViewModelFactory(context))
                .get(OldGamesViewModel.class);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadGames());
    }

    public void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdapter = new OldGamesRvAdapter();
        rvAdapter.setOldGameItemListener(this);
        recyclerView.setAdapter(rvAdapter);
    }

    private void observeViewModel() {
        viewModel.getOldGames().observe(this, this :: setGames);
        viewModel.getProgressState().observe(this, this :: toogleLocalProgress);
        viewModel.getSnackbarMessage().observe(this, this :: showSnackbar);
    }

    private void setGames(List<Game> games) {
        if(games == null || games.isEmpty()) emptyLayout.setVisibility(VISIBLE);
        if(emptyLayout.getVisibility() == VISIBLE) emptyLayout.setVisibility(View.GONE);
        rvAdapter.setGames(games);
        toogleLocalProgress(HIDE);
    }

    private void toogleLocalProgress(ShowState showState) {
        swipeRefreshLayout.setRefreshing(showState == SHOW);
    }

    private void showSnackbar(String message) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG)
                .show();
    }

    //endregion
}
