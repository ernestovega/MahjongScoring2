package es.etologic.mahjongscoring2.app.game.game_table;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.model.ToolbarState;
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils;
import es.etologic.mahjongscoring2.domain.model.PlayerRanking;
import es.etologic.mahjongscoring2.domain.model.RankingTable;
import es.etologic.mahjongscoring2.domain.model.Seat;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;
import es.etologic.mahjongscoring2.domain.model.enums.Winds;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.EnablingState.DISABLED;
import static es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED;
import static es.etologic.mahjongscoring2.app.model.ShowState.*;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.WEST;

public class GameTableFragment extends Fragment {

    //TABLE VIEWS
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
    //RANKING VIEWS
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
    //RESOURCES
    @BindColor(R.color.colorPrimaryDark) int winnerPlayerLayerColor;
    @BindColor(R.color.purpleDarkTransparent) int penalizedPlayerLayerColor;
    @BindDrawable(R.drawable.ic_penalty_white) Drawable penaltyIcon;
    @BindDrawable(R.drawable.ic_trophy_white_18dp) Drawable tsumoIcon;
    @BindDrawable(R.drawable.ic_medal_white) Drawable ronIcon;
    //FIELDS
    @Inject GameActivityViewModelFactory activityViewModelFactory;
    private Unbinder unbinder;
    private GameActivityViewModel activityViewModel;

    //SEATS EVENTS
    @OnClick(R.id.cvSeatEast) public void onEastSeatClick() {
        activityViewModel.onSeatClicked(EAST);
    }
    @OnClick(R.id.cvSeatSouth) public void onSouthSeatClick() {
        activityViewModel.onSeatClicked(SOUTH);
    }
    @OnClick(R.id.cvSeatWest) public void onWestSeatClick() {
        activityViewModel.onSeatClicked(WEST);
    }
    @OnClick(R.id.cvSeatNorth) public void onNorthSeatClick() {
        activityViewModel.onSeatClicked(NORTH);
    }
    //SEATS LAYERS EVENTS
    @OnClick(R.id.llItemPlayerEastLayer) public void onEastSeatLayerClick() {
        activityViewModel.onSeatLayerClicked(EAST);
    }
    @OnClick(R.id.llItemPlayerSouthLayer) public void onSouthLayerClick() {
        activityViewModel.onSeatLayerClicked(SOUTH);
    }
    @OnClick(R.id.llItemPlayerWestLayer) public void onWestLayerClick() {
        activityViewModel.onSeatLayerClicked(WEST);
    }
    @OnClick(R.id.llItemPlayerNorthLayer) public void onNorthLayerClick() {
        activityViewModel.onSeatLayerClicked(NORTH);
    }
    //FABS EVENTS
    @OnClick(R.id.fabGamePenaltyCancel) public void onFabGamePenaltyCancelClick() {
        activityViewModel.onFabGamePenaltyCancelClicked();
    }
    @OnClick(R.id.fabGamePenalty) public void onFabGamePenaltyClick() {
        activityViewModel.onFabGamePenaltyClicked();
    }
    @OnClick(R.id.fabGameWashout) public void onFabGameWashoutClick() {
        activityViewModel.onFabGameWashoutClicked();
    }
    @OnClick(R.id.fabGameTsumo) public void onFabGameTsumoClick() {
        activityViewModel.onFabGameTsumoClicked();
    }
    @OnClick(R.id.fabGameRon) public void onFabGameRonClick() {
        activityViewModel.onFabGameRonClicked();
    }
    @OnClick(R.id.fabCancel) public void onFabCancelLooserSelectionClick() {
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
        setupFabMenu();
        setupActivityViewModel();
    }
    private void setupFabMenu() {
        fabMenuGame.setClosedOnTouchOutside(true);
        fabMenuGame.setOnMenuToggleListener(opened -> activityViewModel.toggleFabMenuOpenState(opened ? SHOW : HIDE));
    }
    private void setupActivityViewModel() {
        //noinspection ConstantConditions
        activityViewModel = ViewModelProviders.of(getActivity(), activityViewModelFactory).get(GameActivityViewModel.class);
        activityViewModel.getFabMenuState().observe(this, this::fabMenuStateObserver);
        activityViewModel.getFabMenuOpenState().observe(this, this::fabMenuOpenStateObserver);
        activityViewModel.getSeats().observe(this, this::seatsObserver);
        activityViewModel.getSelectedSeat().observe(this, this::selectedSeatObserver);
        activityViewModel.getWinnerLayerSeat().observe(this, this::winnerLayerSeatObserver);
        activityViewModel.getPenaltyLayerSeats().observe(this, this::penaltyLayerSeatsObserver);
        activityViewModel.getPenaltyIconSeat().observe(this, this::penaltyIconSeatObserver);
        activityViewModel.getRonIconSeat().observe(this, this::ronIconSeatObserver);
        activityViewModel.getTsumoIconSeat().observe(this, this::tsumoIconSeatObserver);
    }

    private void fabMenuStateObserver(FabMenuStates fabMenuStates) {
        switch (fabMenuStates) {
            default:
            case NORMAL:
                fabPenaltyCancel.setEnabled(false);
                fabPenalty.setEnabled(false);
                fabTsumo.setEnabled(false);
                fabRon.setEnabled(false);
                fabMenuGame.setVisibility(VISIBLE);
                break;
            case PLAYER_SELECTED:
                fabPenaltyCancel.setEnabled(false);
                fabPenalty.setEnabled(true);
                fabTsumo.setEnabled(true);
                fabRon.setEnabled(true);
                fabMenuGame.setVisibility(VISIBLE);
                break;
            case PLAYER_PENALIZED:
                fabPenaltyCancel.setEnabled(true);
                fabPenalty.setEnabled(true);
                fabTsumo.setEnabled(false);
                fabRon.setEnabled(false);
                fabMenuGame.setVisibility(VISIBLE);
                break;
            case CANCEL:
                fabMenuGame.setVisibility(GONE);
                fabCancel.setVisibility(VISIBLE);
                break;
        }
    }

    private void fabMenuOpenStateObserver(ShowState state) {
        if(state == SHOW) {
            fabMenuGame.open(true);
        } else {
            fabMenuGame.close(true);
        }
    }

    private void seatsObserver(Seat[] seats) {
        tvSeatEastName.setText( seats[EAST.getCode() ].getName());
        tvSeatSouthName.setText(seats[SOUTH.getCode()].getName());
        tvSeatWestName.setText( seats[WEST.getCode() ].getName());
        tvSeatNorthName.setText(seats[NORTH.getCode()].getName());

        tvSeatEastPoints.setText( seats[EAST.getCode() ].getPoints());
        tvSeatSouthPoints.setText(seats[SOUTH.getCode()].getPoints());
        tvSeatWestPoints.setText( seats[WEST.getCode() ].getPoints());
        tvSeatNorthPoints.setText(seats[NORTH.getCode()].getPoints());
    }
    private void selectedSeatObserver(Winds wind) {
        switch (wind) {
            case EAST:
                showPlayerLayer(EAST, winnerPlayerLayerColor);
                break;
            case SOUTH:
                showPlayerLayer(SOUTH, winnerPlayerLayerColor);
                break;
            case WEST:
                showPlayerLayer(WEST, winnerPlayerLayerColor);
                break;
            case NORTH:
                showPlayerLayer(NORTH, winnerPlayerLayerColor);
                break;
            default:
            case NONE:
                hidePlayerLayer(EAST);
                hidePlayerLayer(SOUTH);
                hidePlayerLayer(WEST);
                hidePlayerLayer(NORTH);
                break;
        }
    }

    private void winnerLayerSeatObserver(Winds wind) {
        showPlayerLayer(wind, winnerPlayerLayerColor);
    }
    private void penaltyLayerSeatsObserver(boolean[] booleans) {
        if(booleans[EAST.getCode()]) {
            showPlayerLayer(EAST, penalizedPlayerLayerColor);
        }
        if(booleans[SOUTH.getCode()]) {
            showPlayerLayer(SOUTH, penalizedPlayerLayerColor);
        }
        if(booleans[WEST.getCode()]) {
            showPlayerLayer(WEST, penalizedPlayerLayerColor);
        }
        if(booleans[NORTH.getCode()]) {
            showPlayerLayer(NORTH, penalizedPlayerLayerColor);
        }
    }
    private void showPlayerLayer(Winds wind, @ColorInt int color) {
        if(wind == EAST) {
            llSeatEastLayer.setBackgroundColor(color);
            llSeatEastLayer.setVisibility(View.VISIBLE);
        } else if(wind == SOUTH) {
            llSeatSouthLayer.setBackgroundColor(color);
            llSeatSouthLayer.setVisibility(View.VISIBLE);
        } else if(wind == WEST) {
            llSeatWestLayer.setBackgroundColor(color);
            llSeatWestLayer.setVisibility(View.VISIBLE);
        } else {
            llSeatNorthLayer.setBackgroundColor(color);
            llSeatNorthLayer.setVisibility(View.VISIBLE);
        }
    }
    private void hidePlayerLayer(Winds wind) {
        if(wind == EAST) {
            llSeatEastLayer.setVisibility(View.GONE);
            ivSeatEastLayerIcon.setImageDrawable(null);
        } else if(wind == SOUTH) {
            llSeatSouthLayer.setVisibility(View.GONE);
            ivSeatSouthLayerIcon.setImageDrawable(null);
        } else if(wind == WEST) {
            llSeatWestLayer.setVisibility(View.GONE);
            ivSeatWestLayerIcon.setImageDrawable(null);
        } else {
            llSeatNorthLayer.setVisibility(View.GONE);
            ivSeatNorthLayerIcon.setImageDrawable(null);
        }
    }

    private void tsumoIconSeatObserver(Winds wind) {
        setPlayerLayerIcon(wind, tsumoIcon);
    }
    private void ronIconSeatObserver(Winds wind) {
        setPlayerLayerIcon(wind, ronIcon);
    }
    private void penaltyIconSeatObserver(Winds wind) {
        setPlayerLayerIcon(wind, penaltyIcon);
    }
    private void setPlayerLayerIcon(Winds wind, Drawable icon) {
        if(wind == EAST) {
            ivSeatEastLayerIcon.setImageDrawable(icon);
        } else if(wind == SOUTH) {
            ivSeatSouthLayerIcon.setImageDrawable(icon);
        } else if(wind == WEST) {
            ivSeatWestLayerIcon.setImageDrawable(icon);
        } else {
            ivSeatNorthLayerIcon.setImageDrawable(icon);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fabMenuGame.setClosedOnTouchOutside(true);
        fabMenuGame.setOnMenuToggleListener(opened -> activityViewModel.onFamClosed(opened));
    }
    @Override
    public void onDestroyView() {
        if(unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    //DIALOGS
    public void showRequestHandPointsDialog() {
        if(getContext() != null) {
            final LinearLayout linearLayout = getPointsDialogEditTextPoints();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleMM);
            builder.setTitle(R.string.hand_points)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        String text = ((EditText) linearLayout.getChildAt(0)).getText().toString();
                        activityViewModel.onRequestHandPointsResponse(text);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> activityViewModel.onRequestHandPointsCancel())
                    .setView(linearLayout);
            AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            dialog.show();
        }
    }
    private LinearLayout getPointsDialogEditTextPoints() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for(int i = start; i < end; i++) {
                if(!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        editText.setFilters(new InputFilter[] { filter });
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        editText.setFilters(filterArray);
        editText.setLines(1);
        editText.setHint(R.string.enter_points);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    KeyboardUtils.showKeyboard(v.getContext(), v);
                } else {
                    KeyboardUtils.hideKeyboard(v.getContext(), v);
                }
            }
        });
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        layoutParams.setMargins(margin, 0, margin, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.standard_margin));
            layoutParams.setMarginEnd(margin);
        }
        layout.setLayoutParams(layoutParams);
        layout.addView(editText, layoutParams);
        return layout;
    }

    public void showRequestPenaltyPointsDialog() {
        if(getContext() != null) {
            final LinearLayout linearLayout = getPointsDialogEditTextPoints();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleMM);
            builder .setTitle(R.string.penalty_points)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        String text = ((EditText) linearLayout.getChildAt(0)).getText().toString();
                        activityViewModel.onRequestPenaltyPointsResponse(text);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> activityViewModel.onRequestPenaltyPointsCancel())
                    .setView(linearLayout);
            AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            if (window != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            dialog.show();
        }
    }

    //FAB MENU
    public void openFam() {
        fabMenuGame.open(true);
    }
    public void closeFam() {
        fabMenuGame.close(true);
    }

    //PLAYERS LAYERS
    public void enableViewPagerPageChange() {
        activityViewModel.toggleViewPagerPagingState(ENABLED);
    }

    public void disableViewPagerPageChange() {
        activityViewModel.toggleViewPagerPagingState(DISABLED);
    }

    public void setToolbarTitleNormal() {
        activityViewModel.setToolbarState(ToolbarState.NORMAL);
    }

    public void setToolbarTitleSelectLooser() {
        activityViewModel.setToolbarState(ToolbarState.REQUEST_LOOSER);
    }

//    public void finalizeGame() {
//        finishGame();
//    }

    public void showRanking(RankingTable rankingTable) {
        PlayerRanking playerFirst = rankingTable.getSortedPlayersRankings().get(0);
        PlayerRanking playerSecond = rankingTable.getSortedPlayersRankings().get(1);
        PlayerRanking playerThird = rankingTable.getSortedPlayersRankings().get(2);
        PlayerRanking playerFourth = rankingTable.getSortedPlayersRankings().get(3);
        tvNamePlayerFirst.setText(playerFirst.getName());
        tvNamePlayerSecond.setText(playerSecond.getName());
        tvNamePlayerThird.setText(playerThird.getName());
        tvNamePlayerFourth.setText(playerFourth.getName());
        tvPointsPlayerFirst.setText(playerFirst.getPoints());
        tvPointsPlayerSecond.setText(playerSecond.getPoints());
        tvPointsPlayerThird.setText(playerThird.getPoints());
        tvPointsPlayerFourth.setText(playerFourth.getPoints());
        tvScorePlayerFirst.setText(playerFirst.getScore());
        tvScorePlayerSecond.setText(playerSecond.getScore());
        tvScorePlayerThird.setText(playerThird.getScore());
        tvScorePlayerFourth.setText(playerFourth.getScore());
        tvBestHandPlayerPoints.setText(rankingTable.getBestHandPlayerPoints());
        tvNumRounds.setText(rankingTable.getSNumRounds());
        tvDuration.setText(rankingTable.getDuration());
        if(rankingTable.getNumRounds() < 16) {
            fabResume.setVisibility(View.VISIBLE);
            fabResume.setOnClickListener(view -> {/*activityViewModel.resumeGame()*/});
        } else {
            fabResume.setVisibility(View.GONE);
        }
        llGameTableRankingContainer.setVisibility(View.VISIBLE);
    }

    public void hideRanking() {
        llGameTableRankingContainer.setVisibility(View.GONE);
    }

//    public void refreshSeats() {
//        activityViewModel.initFragment(mGameId);
//    }
}
