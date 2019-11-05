package es.etologic.mahjongscoring2.app.main.old_games

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.BaseFragment
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory
import es.etologic.mahjongscoring2.app.model.ShowState
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import kotlinx.android.synthetic.main.oldgames_fragment.*
import javax.inject.Inject

class OldGamesFragment : BaseFragment(), OldGamesRvAdapter.GameItemListener {
    
    private var rvAdapter: OldGamesRvAdapter? = null
    @Inject internal lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
    private var activityViewModel: MainActivityViewModel? = null
    @Inject internal lateinit var oldGamesViewModelFactory: OldGamesViewModelFactory
    private var viewModel: OldGamesViewModel? = null
    
    override fun onOldGameItemDeleteClicked(gameId: Long) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.delete_game)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel?.deleteGame(gameId) }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }
    
    override fun onOldGameItemResumeClicked(gameId: Long) {
        activityViewModel?.startGame(gameId)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.oldgames_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefreshLayout()
        setToolbar()
        setOnClickListeners()
    }
    
    private fun setOnClickListeners() {
        fabOldGames.setOnClickListener {
            activityViewModel?.startGame(null)
        }
    }
    
    private fun setupRecyclerView() {
        recyclerViewOldGames?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        recyclerViewOldGames?.layoutManager = layoutManager
        rvAdapter = OldGamesRvAdapter()
        rvAdapter?.setOldGameItemListener(this)
        recyclerViewOldGames?.adapter = rvAdapter
    }
    
    private fun setupViewModel() {
        if (activity != null) {
            activityViewModel = ViewModelProviders.of(activity!!, mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
            viewModel = ViewModelProviders.of(this, oldGamesViewModelFactory).get(OldGamesViewModel::class.java)
            viewModel?.getError()?.observe(this, Observer { this.showError(it) })
            viewModel?.getGames()?.observe(this, Observer { this.setGames(it) })
            viewModel?.getSnackbarMessage()?.observe(this, Observer { this.showSnackbar(it) })
            viewModel?.getProgressState()?.observe(this, Observer { this.toogleLocalProgress(it) })
        }
    }
    
    private fun setGames(games: List<GameWithRounds>?) {
        if (games == null || games.isEmpty()) {
            emptyLayoutOldGames?.visibility = VISIBLE
        } else {
            emptyLayoutOldGames?.visibility = GONE
            rvAdapter?.setGames(games)
        }
        toogleLocalProgress(HIDE)
    }
    
    private fun toogleLocalProgress(showState: ShowState) {
        swipeRefreshLayoutOldGames?.isRefreshing = showState === SHOW
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(toolbarOldGames, message, Snackbar.LENGTH_LONG).show()
    }
    
    private fun setupSwipeRefreshLayout() {
        if (activity != null) {
            swipeRefreshLayoutOldGames?.setColorSchemeColors(
                ContextCompat.getColor(activity!!, R.color.colorPrimary),
                ContextCompat.getColor(activity!!, R.color.purplePenalty)
            )
            swipeRefreshLayoutOldGames?.setOnRefreshListener { viewModel?.getAllGames() }
        }
    }
    
    private fun setToolbar() {
        activityViewModel?.setToolbar(toolbarOldGames)
    }
    
    override fun onResume() {
        super.onResume()
        viewModel?.getAllGames()
    }
}
