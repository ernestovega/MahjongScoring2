package es.etologic.mahjongscoring2.app.new_game;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.main.IMainToolbarListener;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.Player;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class NewGameFragment extends Fragment {

    //region Fields

    @BindView(R.id.toolbarNewGame) Toolbar toolbar;
    @BindView(R.id.chipsInputNewGame) ChipsInput chipsInput;
    @BindView(R.id.fabNewGame) FloatingActionButton fab;
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
        observeViewModel();
        setupChips();
        hideFab();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mainToolbarListener != null && toolbar != null) {
            mainToolbarListener.setToolbar(toolbar);
        }
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
    }

    //endregion

    //region Events

    @OnClick(R.id.fabNewGame) void onFabNewGameClick() {
        List<Player> players = obtainPlayers(getSelectedPlayerChips());
        viewModel.playersEntered(players);
    }

    //endregion

    //region Private

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideNewGameViewModelFactory(context))
                .get(NewGameViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getPlayers().observe(this, this :: setAllPlayers);
    }

    private void setAllPlayers(List<Player> allPlayers) {
        chipsInput.setFilterableList(createPlayerChips(allPlayers));
    }

    private void toogleProgress(ShowState showState) {

    }

    private void showFab() {
        fab.setVisibility(View.VISIBLE);
    }

    private void hideFab() {
        fab.setVisibility(View.GONE);
    }

    //endregion

    //region Chips

    private void setupChips() {
        viewModel.loadAllPlayers();

        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                if(newSize == 4) {
                    chipsInput.getEditText().setEnabled(false);
                    showFab();
                }
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                if(newSize == 3) {
                    chipsInput.getEditText().setEnabled(true);
                    hideFab();
                }
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
            players.add(new Player(playerChip.getPlayer().getPlayerName()));
        }
        return players;
    }

    //endregion
}
