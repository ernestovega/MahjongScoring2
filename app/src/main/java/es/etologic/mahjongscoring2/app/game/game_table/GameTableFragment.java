package es.etologic.mahjongscoring2.app.game.game_table;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.model.Seat;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;
import es.etologic.mahjongscoring2.domain.model.enums.Winds;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class GameTableFragment extends Fragment {

    //Table Views
    @BindView(R.id.cvSeatEast) CardView cvSeatEast;
    @BindView(R.id.cvSeatSouth) CardView cvSeatSouth;
    @BindView(R.id.cvSeatWest) CardView cvSeatWest;
    @BindView(R.id.cvSeatNorth) CardView cvSeatNorth;
    @BindView(R.id.tvItemPlayerEastName) TextView tvSeatEastName;
    @BindView(R.id.tvItemPlayerSouthName) TextView tvSeatSouthName;
    @BindView(R.id.tvItemPlayerWestName) TextView tvSeatWestName;
    @BindView(R.id.tvItemPlayerNorthName) TextView tvSeatNorthName;
    @BindView(R.id.tvItemPlayerEastPoints) TextView tvSeatEastPoints;
    @BindView(R.id.tvItemPlayerSouthPoints) TextView tvSeatSouthPoints;
    @BindView(R.id.tvItemPlayerWestPoints) TextView tvSeatWestPoints;
    @BindView(R.id.tvItemPlayerNorthPoints) TextView tvSeatNorthPoints;
    @BindView(R.id.llItemPlayerEastLayer) LinearLayout llSeatEastLayer;
    @BindView(R.id.llItemPlayerSouthLayer) LinearLayout llSeatSouthLayer;
    @BindView(R.id.llItemPlayerWestLayer) LinearLayout llSeatWestLayer;
    @BindView(R.id.llItemPlayerNorthLayer) LinearLayout llSeatNorthLayer;
    @BindView(R.id.ivItemPlayerEastLayerIcon) ImageView ivSeatEastLayerIcon;
    @BindView(R.id.ivItemPlayerSouthLayerIcon) ImageView ivSeatSouthLayerIcon;
    @BindView(R.id.ivItemPlayerWestLayerIcon) ImageView ivSeatWestLayerIcon;
    @BindView(R.id.ivItemPlayerNorthLayerIcon) ImageView ivSeatNorthLayerIcon;
    @BindView(R.id.famGame) FloatingActionMenu fabMenuGame;
    @BindView(R.id.fabGamePenaltyCancel) FloatingActionButton fabPenaltyCancel;
    @BindView(R.id.fabGamePenalty) FloatingActionButton fabPenalty;
    @BindView(R.id.fabGameTsumo) FloatingActionButton fabTsumo;
    @BindView(R.id.fabGameRon) FloatingActionButton fabRon;
    @BindView(R.id.fabCancel) FloatingActionButton fabCancel;
    //Ranking Views
    @BindView(R.id.llGameTableRankingContainer) LinearLayout llGameTableRankingContainer;
    @BindView(R.id.tvNamePlayerFirst) TextView tvNamePlayerFirst;
    @BindView(R.id.tvNamePlayerSecond) TextView tvNamePlayerSecond;
    @BindView(R.id.tvNamePlayerThird) TextView tvNamePlayerThird;
    @BindView(R.id.tvNamePlayerFourth) TextView tvNamePlayerFourth;
    @BindView(R.id.tvPointsPlayerFirst) TextView tvPointsPlayerFirst;
    @BindView(R.id.tvPointsPlayerSecond) TextView tvPointsPlayerSecond;
    @BindView(R.id.tvPointsPlayerThird) TextView tvPointsPlayerThird;
    @BindView(R.id.tvPointsPlayerFourth) TextView tvPointsPlayerFourth;
    @BindView(R.id.tvScorePlayerFirst) TextView tvScorePlayerFirst;
    @BindView(R.id.tvScorePlayerSecond) TextView tvScorePlayerSecond;
    @BindView(R.id.tvScorePlayerThird) TextView tvScorePlayerThird;
    @BindView(R.id.tvScorePlayerFourth) TextView tvScorePlayerFourth;
    @BindView(R.id.tvBestHandPlayerPoints) TextView tvBestHandPlayerPoints;
    @BindView(R.id.tvNumRounds) TextView tvNumRounds;
    @BindView(R.id.tvDuration) TextView tvDuration;
    @BindView(R.id.fabRankingResume) FloatingActionButton fabResume;
    //Assets
    @BindColor(R.color.colorPrimaryDark) int winnerPlayerLayerColor;
    @BindColor(R.color.purpleDarkTransparent) int penalizedPlayerLayerColor;
    @BindDrawable(R.drawable.ic_penalty_white) Drawable penaltyIcon;
    @BindDrawable(R.drawable.ic_trophy_white_18dp) Drawable tsumoIcon;
    @BindDrawable(R.drawable.ic_medal_white) Drawable ronIcon;
    //Fields
    @Inject GameActivityViewModelFactory activityViewModelFactory;
    private Unbinder unbinder;
    private GameActivityViewModel activityViewModel;

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_table_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        setupFabMenu();
        setupActivityViewModel();
    }
    private void setupFabMenu() {
        fabMenuGame.setClosedOnTouchOutside(true);
        fabMenuGame.setOnMenuToggleListener(opened -> activityViewModel.toggleFabMenuShowState(opened));
    }
    private void setupActivityViewModel() {
        //noinspection ConstantConditions
        activityViewModel = ViewModelProviders.of(getActivity(), activityViewModelFactory).get(GameActivityViewModel.class);
        activityViewModel.getFabMenuShowState().observe(this, this::fabMenuShowStateObserver);
        activityViewModel.getFabMenuState().observe(this, this::fabMenuStateObserver);
        activityViewModel.getSeats().observe(this, this::seatsObserver);
        activityViewModel.getSelectedSeat().observe(this, this::selectedSeatObserver);
        activityViewModel.getWinnerLayerSeat().observe(this, this::winnerLayerSeatObserver);
        activityViewModel.getLooserLayerSeat().observe(this, this::looserLayerSeatObserver);
        activityViewModel.getPenaltyLayerSeats().observe(this, this::penaltyLayerSeatsObserver);
        activityViewModel.getPenaltyIconSeat().observe(this, this::penaltyIconSeatObserver);
        activityViewModel.getRonIconSeat().observe(this, this::ronIconSeatObserver);
        activityViewModel.getTsumoIconSeat().observe(this, this::tsumoIconSeatObserver);
    }
    private void fabMenuShowStateObserver(ShowState showState) {
        fabMenuGame.setVisibility(showState == ShowState.SHOW ? VISIBLE : GONE);
    }
    private void fabMenuStateObserver(FabMenuStates fabMenuStates) {
        switch (fabMenuStates) {
            default:
            case NORMAL:
                break;
            case PLAYER_SELECTED:
                break;
            case PLAYER_PENALIZED:
                break;
            case CANCEL:
                break;
        }
    }
    private void seatsObserver(Seat[] seats) {

    }
    private void selectedSeatObserver(Winds winds) {

    }
    private void winnerLayerSeatObserver(Winds winds) {

    }
    private void looserLayerSeatObserver(Winds winds) {

    }
    private void penaltyLayerSeatsObserver(boolean[] booleans) {

    }
    private void penaltyIconSeatObserver(Winds winds) {

    }
    private void ronIconSeatObserver(Winds winds) {

    }
    private void tsumoIconSeatObserver(Winds winds) {

    }
}
