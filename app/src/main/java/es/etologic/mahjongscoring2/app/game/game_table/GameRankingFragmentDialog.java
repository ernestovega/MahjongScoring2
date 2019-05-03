package es.etologic.mahjongscoring2.app.game.game_table;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory;
import es.etologic.mahjongscoring2.app.model.GamePages;
import es.etologic.mahjongscoring2.domain.model.PlayerRanking;
import es.etologic.mahjongscoring2.domain.model.RankingTable;

public class GameRankingFragmentDialog extends DialogFragment {

    //Constants
    public static final String TAG = "GameRankingFragmentDialog";

    //Views
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
    //    @BindView(R.id.tvDuration) TextView tvDuration;
    @BindView(R.id.btRankingDialogSeeList) Button btSeeList;
    @BindView(R.id.btRankingDialogResume) Button btResume;
    @BindView(R.id.btRankingDialogExit) Button btExit;
    //FIELDS
    @Inject GameActivityViewModelFactory activityViewModelFactory;
    private Unbinder unbinder;
    private GameActivityViewModel activityViewModel;

    //EVENTS
    @OnClick(R.id.btRankingDialogSeeList) public void onRankingDialogSeeListClick() {
        activityViewModel.setCurrentViewPagerPage(GamePages.LIST);
        dismiss();
    }
    @OnClick(R.id.btRankingDialogResume) public void onRankingDialogResumeClick() {
        activityViewModel.resumeGame();
    }
    @OnClick(R.id.btRankingDialogClose) public void onRankingDialogCloseClick() {
        dismiss();
    }
    @OnClick(R.id.btRankingDialogExit) public void onRankingDialogExitClick() {
        activityViewModel.exit();
        dismiss();
    }

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_table_ranking_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        setCancelable(false);
        setupActivityViewModel();
        fillRankingViews();
    }
    private void setupActivityViewModel() {
        if (getActivity() != null) {
            activityViewModel = ViewModelProviders.of(getActivity(), activityViewModelFactory).get(GameActivityViewModel.class);
        } else {
            dismiss();
        }
    }
    private void fillRankingViews() {
        RankingTable rankingTable = activityViewModel.getRankingTable();
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
        //        tvDuration.setText(rankingTable.getDuration());
        if (rankingTable.getNumRounds() < 16) {
            btResume.setVisibility(View.VISIBLE);
            btResume.setOnClickListener(view -> activityViewModel.resumeGame());
        } else {
            btResume.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
