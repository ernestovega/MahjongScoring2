package es.etologic.mahjongscoring2.app.game.game_list;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class GameListFragment extends Fragment {

    //region Fields

    @BindView(R.id.rvGameList) RecyclerView recyclerView;
    @BindView(R.id.tvGameListHeaderNameP1) TextView tvHeaderNameP1;
    @BindView(R.id.tvGameListHeaderNameP2) TextView tvHeaderNameP2;
    @BindView(R.id.tvGameListHeaderNameP3) TextView tvHeaderNameP3;
    @BindView(R.id.tvGameListHeaderNameP4) TextView tvHeaderNameP4;
    @BindView(R.id.tvGameListFooterTotalPointsP1) TextView tvFooterTotalPointsP1;
    @BindView(R.id.tvGameListFooterTotalPointsP2) TextView tvFooterTotalPointsP2;
    @BindView(R.id.tvGameListFooterTotalPointsP3) TextView tvFooterTotalPointsP3;
    @BindView(R.id.tvGameListFooterTotalPointsP4) TextView tvFooterTotalPointsP4;
    private Unbinder unbinder;
    @Inject GameActivityViewModelFactory activityViewModelFactory;
    private GameActivityViewModel activityViewModel;
    private GameListRvAdapter rvAdapter;

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        setupRecyclerView();
        setupActivityViewModel();
        activityViewModel.getGame();
    }
    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new GameListRvAdapter();
        recyclerView.setAdapter(rvAdapter);
    }
    private void setupActivityViewModel() {
        //noinspection ConstantConditions
        activityViewModel = ViewModelProviders.of(getActivity(), activityViewModelFactory).get(GameActivityViewModel.class);
    }
    @Override public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
    //    public void fillHeaderPlayersNames(List<String> playersNames) {
//        tvHeaderNameP1.setText(playersNames.get(0));
//        tvHeaderNameP2.setText(playersNames.get(1));
//        tvHeaderNameP3.setText(playersNames.get(2));
//        tvHeaderNameP4.setText(playersNames.get(3));
//    }
//
//    public void fillFooterTotalPoints(List<String> playersTotalPoints) {
//        tvFooterTotalPointsP1.setText(playersTotalPoints.get(0));
//        tvFooterTotalPointsP2.setText(playersTotalPoints.get(1));
//        tvFooterTotalPointsP3.setText(playersTotalPoints.get(2));
//        tvFooterTotalPointsP4.setText(playersTotalPoints.get(3));
//    }
}
