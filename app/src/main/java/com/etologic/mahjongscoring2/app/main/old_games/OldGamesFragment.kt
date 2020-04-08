package com.etologic.mahjongscoring2.app.main.old_games

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.main.activity.MainActivity
import com.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.GameStartType
import com.etologic.mahjongscoring2.business.model.enums.GameStartType.NEW
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
    private lateinit var activityViewModel: MainViewModel
    private lateinit var viewModel: OldGamesViewModel
    
    @Inject
    internal lateinit var rvAdapter: OldGamesRvAdapter
    
    //EVENTS
    override fun onOldGameItemDeleteClicked(gameId: Long) {
        AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
            .setTitle(R.string.delete_game)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteGame(gameId) }
            .setNegativeButton(R.string.cancel, null)
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
        rvOldGames?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        rvOldGames?.layoutManager = layoutManager
        rvAdapter.setOldGameItemListener(this)
        rvOldGames?.adapter = rvAdapter
    }
    
    private fun setupViewModel() {
        if (activity != null) {
            activityViewModel = ViewModelProvider(activity!!, mainActivityViewModelFactory).get(MainViewModel::class.java)
            viewModel = ViewModelProvider(this, oldGamesViewModelFactory).get(OldGamesViewModel::class.java)
            viewModel.getError().observe(viewLifecycleOwner, Observer(this::showError))
            viewModel.getProgressState().observe(viewLifecycleOwner, Observer((activity as MainActivity)::toggleProgress))
            viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer { view?.let { view -> showSnackbar(view, it) } })
            viewModel.getGames().observe(viewLifecycleOwner, Observer(this::gamesObserver))
            viewModel.getStartGame().observe(viewLifecycleOwner, Observer(this::startGameObserver))
        }
    }
    
    private fun gamesObserver(games: List<Table>) {
        if (games.isEmpty())
            emptyLayoutOldGames?.visibility = VISIBLE
        else {
            emptyLayoutOldGames?.visibility = GONE
            rvAdapter.setGames(games)
            if (activityViewModel.lastGameStartType == NEW) {
                activityViewModel.lastGameStartType = null
                rvOldGames?.smoothScrollToPosition(0)
            }
        }
    }
    
    private fun startGameObserver(gameStartType: GameStartType) {
        activityViewModel.lastGameStartType = gameStartType
        activityViewModel.navigateTo(GAME)
    }
    
    private fun setToolbar() {
        activityViewModel.setToolbar(toolbarOldGames)
    }
    
    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayoutOldGames?.setOnRefreshListener {
            viewModel.getAllGames()
            swipeRefreshLayoutOldGames.isRefreshing = false
        }
    }
    
    private fun setOnClickListeners() {
        fabOldGames?.setOnSecureClickListener {
            viewModel.startNewGame()
        }
    }
}
