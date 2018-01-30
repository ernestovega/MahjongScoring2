package es.etologic.mahjongscoring2.app.combinations;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.main.IMainToolbarListener;
import es.etologic.mahjongscoring2.domain.entities.Combination;

public class CombinationsFragment extends Fragment {

    //region Fields

    @BindView(R.id.toolbarCombinations) Toolbar toolbar;
    @BindView (R.id.recyclerViewCombinations) RecyclerView recyclerView;
    private Unbinder unbinder;
//    private CombinationsRvAdapter rvAdapter;
    private Context context;
    private CombinationsViewModel viewModel;
    private IMainToolbarListener mainToolbarListener;

    //endregion

    //region Lifecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combinations_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
//        setupViewModel();
//        setupRecyclerView();
//        observeViewModel();
//        viewModel.loadCombinations();
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

    //region Private

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideCombinationsViewModelFactory(context))
                .get(CombinationsViewModel.class);
    }

    public void setupRecyclerView() {
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvAdapter = new CombinationsRvAdapter();
//        recyclerView.setAdapter(rvAdapter);
    }

    private void observeViewModel() {
        viewModel.getCombinations().observe(this, this ::setCombinations);
    }

    private void setCombinations(List<Combination> combinations) {
//        rvAdapter.setCombinations(combinations);
    }

    //endregion
}
