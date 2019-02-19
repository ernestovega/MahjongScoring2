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
import es.etologic.mahjongscoring2.app.model.EnablingState;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.EnablingState.DISABLED;
import static es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class GameTableFragment extends Fragment {

    //VIEWS
//    @BindView(R.id.tvGameTableRoundNumber) TextView tvRoundNumber;
//    @BindView(R.id.ivGameTableRoundWind) ImageView ivRoundWind;
    @BindView(R.id.famGameTable) FloatingActionMenu fabMenu;
    @BindView(R.id.fabGameTablePenaltyCancel) FloatingActionButton fabPenaltyCancel;
    @BindView(R.id.fabGameTablePenalty) FloatingActionButton fabPenalty;
    @BindView(R.id.fabGameTableTsumo) FloatingActionButton fabTsumo;
    @BindView(R.id.fabGameTableRon) FloatingActionButton fabRon;
    @BindView(R.id.fabGameTableCancel) FloatingActionButton fabCancel;

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

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_table_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        if (getActivity() != null) {
            tableSeats = (GameTableSeatsFragment) getChildFragmentManager().findFragmentById(R.id.fGameTableSeats);
            setupActivityViewModel();
            setupFabMenu();
        }
    }
    private void setupFabMenu() {
        fabMenu.setClosedOnTouchOutside(true);
        fabMenu.setOnMenuToggleListener(activityViewModel::onToggleFabMenu);
    }
    private void setupActivityViewModel() {
        //noinspection ConstantConditions
        activityViewModel = ViewModelProviders.of(getActivity(), activityViewModelFactory).get(GameActivityViewModel.class);
        activityViewModel.getEastSeat().observe(this, tableSeats::setEastSeat);
        activityViewModel.getSouthSeat().observe(this, tableSeats::setSouthSeat);
        activityViewModel.getWestSeat().observe(this, tableSeats::setWestSeat);
        activityViewModel.getNorthSeat().observe(this, tableSeats::setNorthSeat);
        activityViewModel.getRoundNumber().observe(this, this::roundNumberObserver);
        activityViewModel.getFabMenuState().observe(this, this::fabMenuStateObserver);
        activityViewModel.getFabMenuOpenState().observe(this, this::fabMenuOpenStateObserver);
    }
    private void fabMenuStateObserver(FabMenuStates fabMenuStates) {
        switch (fabMenuStates) {
            default:
            case NORMAL:
                applyFabMenuState(DISABLED, DISABLED, DISABLED, DISABLED);
                showFabMenuIfProceed();
                break;
            case PLAYER_SELECTED:
                applyFabMenuState(DISABLED, ENABLED, ENABLED, ENABLED);
                showFabMenuIfProceed();
                break;
            case PLAYER_PENALIZED:
                applyFabMenuState(ENABLED, ENABLED, DISABLED, DISABLED);
                showFabMenuIfProceed();
                break;
            case CANCEL:
                fabMenu.setVisibility(GONE);
                fabCancel.setVisibility(VISIBLE);
                break;
        }
    }
    private void applyFabMenuState(EnablingState penaltyCancel, EnablingState penalty, EnablingState tsumo, EnablingState ron) {
        fabPenaltyCancel.setEnabled(penaltyCancel == ENABLED);
        fabPenalty.setEnabled(penalty == ENABLED);
        fabTsumo.setEnabled(tsumo == ENABLED);
        fabRon.setEnabled(ron == ENABLED);
    }
    private void showFabMenuIfProceed() {
        if (fabMenu.getVisibility() != VISIBLE) {
            fabCancel.setVisibility(GONE);
            fabMenu.setVisibility(VISIBLE);
        }
    }
    private void fabMenuOpenStateObserver(ShowState state) {
        if (state == SHOW) {
            fabMenu.open(true);
        } else {
            fabMenu.close(true);
        }
    }
    private void roundNumberObserver(int roundNumber) {
//        tvRoundNumber.setText(String.valueOf(roundNumber));
//        if (roundNumber < 5)
//            ivRoundWind.setImageDrawable(eastIcon);
//        else if (roundNumber < 9)
//            ivRoundWind.setImageDrawable(southIcon);
//        else if (roundNumber < 13)
//            ivRoundWind.setImageDrawable(westIcon);
//        else
//            ivRoundWind.setImageDrawable(northIcon);
    }
    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
