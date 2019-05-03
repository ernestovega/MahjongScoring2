package es.etologic.mahjongscoring2.app.game.game_list;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import es.etologic.mahjongscoring2.app.base.BaseFragment;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class GameListFragment extends BaseFragment {

    //Views
    @BindView(R.id.rvGameList) RecyclerView rvGameList;
    @BindView(R.id.tvGameListHeaderNameP1) TextView tvHeaderNameP1;
    @BindView(R.id.tvGameListHeaderNameP2) TextView tvHeaderNameP2;
    @BindView(R.id.tvGameListHeaderNameP3) TextView tvHeaderNameP3;
    @BindView(R.id.tvGameListHeaderNameP4) TextView tvHeaderNameP4;
    @BindView(R.id.tvGameListFooterTotalPointsP1) TextView tvFooterTotalPointsP1;
    @BindView(R.id.tvGameListFooterTotalPointsP2) TextView tvFooterTotalPointsP2;
    @BindView(R.id.tvGameListFooterTotalPointsP3) TextView tvFooterTotalPointsP3;
    @BindView(R.id.tvGameListFooterTotalPointsP4) TextView tvFooterTotalPointsP4;
    //Fields
    @Inject GameActivityViewModelFactory activityViewModelFactory;
    @Inject GameListRvAdapter rvAdapter;
    private Unbinder unbinder;
    private GameActivityViewModel activityViewModel;

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
    }
    private void setupRecyclerView() {
        rvGameList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        rvGameList.setLayoutManager(layoutManager);
        rvGameList.setAdapter(rvAdapter);
    }
    private void setupActivityViewModel() {
        //noinspection ConstantConditions
        activityViewModel = ViewModelProviders.of(getActivity(), activityViewModelFactory).get(GameActivityViewModel.class);
        activityViewModel.getListNames().observe(this, this::namesObserver);
        activityViewModel.getListRounds().observe(this, rvAdapter::setCollection);
        activityViewModel.getListTotals().observe(this, this::totalsObserver);
    }
    private void namesObserver(String[] names) {
        tvHeaderNameP1.setText(names[0]);
        tvHeaderNameP2.setText(names[1]);
        tvHeaderNameP3.setText(names[2]);
        tvHeaderNameP4.setText(names[3]);
    }
    private void totalsObserver(String[] totals) {
        tvFooterTotalPointsP1.setText(totals[0]);
        tvFooterTotalPointsP2.setText(totals[1]);
        tvFooterTotalPointsP3.setText(totals[2]);
        tvFooterTotalPointsP4.setText(totals[3]);
    }
    @Override public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
