package es.etologic.mahjongscoring2.app.game.game_list;

import android.content.Context;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel;

public class GameListFragment extends Fragment {

    //region Fields

    @BindView(R.id.rvGameList)
    RecyclerView recyclerView;
    @BindView(R.id.tvGameListHeaderNameP1)
    TextView tvHeaderNameP1;
    @BindView(R.id.tvGameListHeaderNameP2)
    TextView tvHeaderNameP2;
    @BindView(R.id.tvGameListHeaderNameP3)
    TextView tvHeaderNameP3;
    @BindView(R.id.tvGameListHeaderNameP4)
    TextView tvHeaderNameP4;
    @BindView(R.id.tvGameListFooterTotalPointsP1)
    TextView tvFooterTotalPointsP1;
    @BindView(R.id.tvGameListFooterTotalPointsP2)
    TextView tvFooterTotalPointsP2;
    @BindView(R.id.tvGameListFooterTotalPointsP3)
    TextView tvFooterTotalPointsP3;
    @BindView(R.id.tvGameListFooterTotalPointsP4)
    TextView tvFooterTotalPointsP4;

    private Context context;
    private Unbinder unbinder;
    private GameActivityViewModel mainViewModel;
    private GameListRvAdapter rvAdapter;

    //endregion

    //region Public

    public void setMainViewModel(GameActivityViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    //endregion

    //region Lifecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.game_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        setupViewModel();
        observeViewModel();
    }

    //endregion

    //region Private
    private void setupViewModel() {

    }
    private void observeViewModel() {
    }
    public void setupRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new GameListRvAdapter();
        recyclerView.setAdapter(rvAdapter);
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

    //endregion
}
