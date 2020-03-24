package com.etologic.mahjongscoring2.app.main.old_games

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel
import com.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory
import com.etologic.mahjongscoring2.app.model.ShowState
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import kotlinx.android.synthetic.main.main_oldgames_fragment.*
import javax.inject.Inject

class OldGamesFragment : BaseFragment(), OldGamesRvAdapter.GameItemListener {
    
    companion object {
        const val TAG = "OldGamesFragment"
    }
    
    
    @Inject
    internal lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
    @Inject
    internal lateinit var oldGamesViewModelFactory: OldGamesViewModelFactory
    private lateinit var activityViewModel: MainActivityViewModel
    private lateinit var viewModel: OldGamesViewModel
    @Inject
    internal lateinit var rvAdapter: OldGamesRvAdapter
    
    //EVENTS
    override fun onOldGameItemDeleteClicked(gameId: Long) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.delete_game)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteGame(gameId) }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }
    
    override fun onOldGameItemResumeClicked(gameId: Long) {
        viewModel.startGame(gameId)
    }
    
    //LIFECYCLE
    override fun onResume() {
        super.onResume()
        viewModel.getAllGames()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_oldgames_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefreshLayout()
        setToolbar()
        setOnClickListeners()
    }
    
    private fun setupRecyclerView() {
        recyclerViewOldGames?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerViewOldGames?.layoutManager = layoutManager
        rvAdapter.setOldGameItemListener(this)
        recyclerViewOldGames?.adapter = rvAdapter
    }
    
    private fun setupViewModel() {
        if (activity != null) {
            activityViewModel = ViewModelProvider(activity!!, mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
            viewModel = ViewModelProvider(this, oldGamesViewModelFactory).get(OldGamesViewModel::class.java)
            viewModel.getError().observe(viewLifecycleOwner, Observer(this::showError))
            viewModel.getProgressState().observe(viewLifecycleOwner, Observer(this::toogleLocalProgress))
            viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer { view?.let { view -> showSnackbar(view, it) } })
            viewModel.getGames().observe(viewLifecycleOwner, Observer(this::gamesObserver))
            viewModel.getStartGame().observe(viewLifecycleOwner, Observer { activityViewModel.navigateTo(GAME) })
        }
    }
    
    private fun gamesObserver(games: List<GameWithRounds>) {
        if (games.isEmpty())
            emptyLayoutOldGames?.visibility = VISIBLE
        else {
            emptyLayoutOldGames?.visibility = GONE
            rvAdapter.setGames(games)
        }
    }
    
    private fun toogleLocalProgress(showState: ShowState) {
        swipeRefreshLayoutOldGames.isRefreshing = showState == SHOW
        llOldGamesProgress?.visibility = if (showState === SHOW) VISIBLE else GONE
    }
    
    private fun setToolbar() {
        activityViewModel.setToolbar(toolbarOldGames)
    }
    
    private fun setupSwipeRefreshLayout() {
        if (activity != null) {
            swipeRefreshLayoutOldGames?.setColorSchemeColors(
                ContextCompat.getColor(activity!!, R.color.colorPrimary),
                ContextCompat.getColor(activity!!, R.color.colorAccent),
                ContextCompat.getColor(activity!!, R.color.purplePenalty),
                ContextCompat.getColor(activity!!, R.color.colorPrimaryDark)
            )
            swipeRefreshLayoutOldGames?.setOnRefreshListener { viewModel.getAllGames() }
        }
    }
    
    private fun setOnClickListeners() {
        fabOldGames?.setOnClickListener {
            viewModel.startNewGame()
        }
    }
}
