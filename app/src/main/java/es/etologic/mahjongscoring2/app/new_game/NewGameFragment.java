package es.etologic.mahjongscoring2.app.new_game;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.main.IMainToolbarListener;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.Player;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class NewGameFragment extends Fragment {

    //region Fields

    @BindView(R.id.toolbarNewGame) Toolbar toolbar;
    @BindView(R.id.swipeRefreshLayoutNewGame) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.chipsViewNewGame) ChipsInput chipsInput;
    private Unbinder unbinder;
    private Context context;
    private NewGameViewModel viewModel;
    private IMainToolbarListener mainToolbarListener;

    //endregion

    //region Lifecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newgame_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        setupViewModel();
        setupSwipeRefreshLayout();
        observeViewModel();
        setupChips();
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbar();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    //endregion

    //region Public

    public void setMainToolbarListener(IMainToolbarListener mainToolbarListener) {
        this.mainToolbarListener = mainToolbarListener;
        setToolbar();
    }

    //endregion

    //region Events

    //endregion

    //region Listener

    //endregion

    //region Private

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideNewGameViewModelFactory(context))
                .get(NewGameViewModel.class);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getIntArray(R.array.swipeRefreshColors));
//        swipeRefreshLayout.setEnabled(false);
//        swipeRefreshLayout.setNestedScrollingEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadAllPlayers());
    }

    private void observeViewModel() {
        viewModel.getPlayers().observe(this, this :: setAllPlayers);
    }

    private void setToolbar() {
        if(mainToolbarListener != null && toolbar != null) {
            mainToolbarListener.setToolbar(toolbar);
        }
    }

    private void setAllPlayers(List<Player> allPlayers) {
        chipsInput.setFilterableList(createPlayerChips(allPlayers));
        toogleLocalProgress(HIDE);
    }

    private void toogleLocalProgress(ShowState showState) {
        swipeRefreshLayout.setRefreshing(showState == SHOW);
    }

    private void showSnackbar(String message) {
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }

    //endregion

    //region Chips

    private void setupChips() {
        viewModel.loadAllPlayers();

        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                // chip added
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                // chip removed
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onTextChanged(CharSequence text) {
                // text changed
            }
        });
    }

    private List<PlayerChip> getSelectedPlayerChips() {
        //noinspection unchecked
        return (List<PlayerChip>) chipsInput.getSelectedChipList();
    }

    private List<PlayerChip> createPlayerChips(List<Player> players) {
        List<PlayerChip> playersChips = new ArrayList<>();
        for(Player player : players) {
            playersChips.add(new PlayerChip(player));
        }
        return playersChips;
    }

    private List<Player> obtainPlayers(List<PlayerChip> playersChips) {
        List<Player> players = new ArrayList<>();
        for(PlayerChip playerChip : playersChips) {
            players.add(new Player(playerChip.getPlayer().getName()));
        }
        return players;
    }

    //endregion
}
