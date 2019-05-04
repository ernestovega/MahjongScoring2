package es.etologic.mahjongscoring2.app.game.game_table;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.model.DialogType;
import es.etologic.mahjongscoring2.app.model.EnablingState;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.EnablingState.DISABLED;
import static es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

public class GameTableFragment extends Fragment implements GameTableSeatsFragment.TableSeatsListener {

    //VIEWS
    @BindView(R.id.ivGameTableRoundWindUp) ImageView ivRoundWindUp;
    @BindView(R.id.tvGameTableRoundNumberUp) TextView tvRoundNumberUp;
    @BindView(R.id.ivGameTableRoundWindDown) ImageView ivRoundWindDown;
    @BindView(R.id.tvGameTableRoundNumberDown) TextView tvRoundNumberDown;
    @BindView(R.id.famGameTable) FloatingActionMenu famMenu;
    @BindView(R.id.fabGameTablePenaltyCancel) FloatingActionButton fabPenaltyCancel;
    @BindView(R.id.fabGameTablePenalty) FloatingActionButton fabPenalty;
    @BindView(R.id.fabGameTableTsumo) FloatingActionButton fabTsumo;
    @BindView(R.id.fabGameTableRon) FloatingActionButton fabRon;
    @BindView(R.id.fabGameTableCancel) FloatingActionButton fabCancel;
    @BindView(R.id.fabGameTableRanking) FloatingActionButton fabRanking;

    //RESOURCES
    @BindDrawable(R.drawable.ic_east) Drawable eastIcon;
    @BindDrawable(R.drawable.ic_south) Drawable southIcon;
    @BindDrawable(R.drawable.ic_west) Drawable westIcon;
    @BindDrawable(R.drawable.ic_north) Drawable northIcon;
    //FIELDS
    @Inject GameActivityViewModelFactory activityViewModelFactory;
    private Unbinder unbinder;
    private GameActivityViewModel activityViewModel;
    private GameTableSeatsFragment tableSeats;

    //EVENTS
    @OnClick(R.id.fabGameTablePenaltyCancel) public void onFabGamePenaltyCancelClick() {
        activityViewModel.onFabGamePenaltyCancelClicked();
    }
    @OnClick(R.id.fabGameTablePenalty) public void onFabGamePenaltyClick() {
        activityViewModel.onFabGamePenaltyClicked();
    }
    @OnClick(R.id.fabGameTableWashout) public void onFabGameWashoutClick() {
        activityViewModel.onFabGameWashoutClicked();
    }
    @OnClick(R.id.fabGameTableTsumo) public void onFabGameTsumoClick() {
        activityViewModel.onFabGameTsumoClicked();
    }
    @OnClick(R.id.fabGameTableRon) public void onFabGameRonClick() {
        activityViewModel.onFabGameRonClicked();
    }
    @OnClick(R.id.fabGameTableCancel) public void onFabCancelLooserSelectionClick() {
        activityViewModel.onFabCancelRequestingLooserClicked();
    }
    @OnClick(R.id.fabGameTableRanking) public void onFabRankingClick() {
        activityViewModel.onFabRankingClicked();
    }
    @Override public void onEastSeatClick() {
        activityViewModel.onSeatClicked(EAST);
    }
    @Override public void onSouthSeatClick() {
        activityViewModel.onSeatClicked(SOUTH);
    }
    @Override public void onWestSeatClick() {
        activityViewModel.onSeatClicked(WEST);
    }
    @Override public void onNorthSeatClick() {
        activityViewModel.onSeatClicked(NORTH);
    }

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_table_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        tableSeats = (GameTableSeatsFragment) getChildFragmentManager().findFragmentById(R.id.fGameTableSeats);
        setupActivityViewModel();
        setupFabMenu();
        tableSeats.setTableSeatsListener(this);
    }
    private void setupFabMenu() {
        famMenu.setClosedOnTouchOutside(true);
        famMenu.setOnMenuToggleListener(activityViewModel::onToggleFabMenu);
    }
    private void setupActivityViewModel() {
        //noinspection ConstantConditions
        activityViewModel = ViewModelProviders.of(getActivity(), activityViewModelFactory).get(GameActivityViewModel.class);
        activityViewModel.getRoundNumber().observe(this, this::roundNumberObserver);
        activityViewModel.getEastSeat().observe(this, tableSeats::setEastSeat);
        activityViewModel.getSouthSeat().observe(this, tableSeats::setSouthSeat);
        activityViewModel.getWestSeat().observe(this, tableSeats::setWestSeat);
        activityViewModel.getNorthSeat().observe(this, tableSeats::setNorthSeat);
        activityViewModel.getFabMenuState().observe(this, this::fabMenuStateObserver);
        activityViewModel.getFabMenuOpenState().observe(this, this::fabMenuOpenStateObserver);
        activityViewModel.getShowDialog().observe(this, this::showDialogObserver);
    }
    private void roundNumberObserver(int roundNumber) {
        if (roundNumber == 16) {
            tvRoundNumberUp.setText(String.format("%s - End", roundNumber));
            tvRoundNumberDown.setText(String.format("%s - End", roundNumber));
        } else {
            tvRoundNumberUp.setText(String.valueOf(roundNumber));
            tvRoundNumberDown.setText(String.valueOf(roundNumber));
        }
        if (roundNumber < 5) {
            ivRoundWindUp.setImageDrawable(eastIcon);
            ivRoundWindDown.setImageDrawable(eastIcon);
        } else if (roundNumber < 9) {
            ivRoundWindUp.setImageDrawable(southIcon);
            ivRoundWindDown.setImageDrawable(southIcon);
        } else if (roundNumber < 13) {
            ivRoundWindUp.setImageDrawable(westIcon);
            ivRoundWindDown.setImageDrawable(westIcon);
        } else {
            ivRoundWindUp.setImageDrawable(northIcon);
            ivRoundWindDown.setImageDrawable(northIcon);
        }
    }
    private void fabMenuStateObserver(FabMenuStates fabMenuStates) {
        switch (fabMenuStates) {
            default:
            case NORMAL:
                applyFabMenuState(DISABLED, DISABLED, DISABLED, DISABLED);
                fabRanking.setVisibility(GONE);
                fabCancel.setVisibility(GONE);
                break;
            case PLAYER_SELECTED:
                applyFabMenuState(DISABLED, ENABLED, ENABLED, ENABLED);
                fabRanking.setVisibility(GONE);
                fabCancel.setVisibility(GONE);
                break;
            case PLAYER_PENALIZED:
                applyFabMenuState(ENABLED, ENABLED, DISABLED, DISABLED);
                fabRanking.setVisibility(GONE);
                fabCancel.setVisibility(GONE);
                break;
            case RANKING:
                famMenu.setVisibility(GONE);
                fabCancel.setVisibility(GONE);
                fabRanking.setVisibility(VISIBLE);
            case HIDDEN:
                fabRanking.setVisibility(GONE);
                famMenu.setVisibility(GONE);
                fabCancel.setVisibility(GONE);
            case CANCEL:
                fabRanking.setVisibility(GONE);
                famMenu.setVisibility(GONE);
                fabCancel.setVisibility(VISIBLE);
                break;
        }
    }
    private void applyFabMenuState(EnablingState penaltyCancelState,
                                   EnablingState penaltyState,
                                   EnablingState tsumoState,
                                   EnablingState ronState) {
        if (penaltyCancelState == ENABLED) {
            fabPenaltyCancel.setEnabled(true);
            fabPenaltyCancel.setVisibility(VISIBLE);
        } else {
            fabPenaltyCancel.setEnabled(false);
            fabPenaltyCancel.setVisibility(GONE);
        }
        fabPenalty.setEnabled(penaltyState == ENABLED);
        fabTsumo.setEnabled(tsumoState == ENABLED);
        fabRon.setEnabled(ronState == ENABLED);
    }
    private void fabMenuOpenStateObserver(ShowState state) {
        if (state == SHOW) {
            if (famMenu.getVisibility() != VISIBLE) {
                famMenu.setVisibility(VISIBLE);
            }
            if(!famMenu.isOpened()) {
                famMenu.open(true);
            }
        } else {
            if(famMenu.isOpened()) {
                famMenu.close(true);
            }
            if (famMenu.getVisibility() == VISIBLE) {
                famMenu.setVisibility(INVISIBLE);
            }
        }
    }
    private void showDialogObserver(DialogType dialogType) {
        if (dialogType == DialogType.SHOW_RANKING) {
            showRankingDialog();
        }
    }
    private void showRankingDialog() {
        new GameRankingFragmentDialog().show(getChildFragmentManager(), GameRankingFragmentDialog.TAG);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
