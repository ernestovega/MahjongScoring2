package es.etologic.mahjongscoring2.app.game.game_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.BaseFragment
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory
import kotlinx.android.synthetic.main.game_list_fragment.*
import javax.inject.Inject

class GameListFragment : BaseFragment() {
    
    //Fields
    @Inject internal lateinit var activityViewModelFactory: GameActivityViewModelFactory
    private lateinit var activityViewModel: GameActivityViewModel
    @Inject internal lateinit var rvAdapter: GameListRvAdapter
    
    //LIFECYCLE
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_list_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupActivityViewModel()
    }
    
    private fun setupRecyclerView() {
        rvGameList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        layoutManager.stackFromEnd = true
        rvGameList.layoutManager = layoutManager
        rvGameList.adapter = rvAdapter
    }
    
    private fun setupActivityViewModel() {
        if (activity != null) {
            activityViewModel = ViewModelProviders.of(activity!!, activityViewModelFactory).get(GameActivityViewModel::class.java)
            activityViewModel.listNames.observe(this, Observer { it?.let { this.namesObserver(it) } })
            activityViewModel.listRounds.observe(this, Observer { rvAdapter.updateCollection(it) })
            activityViewModel.listTotals.observe(this, Observer { it?.let { this.totalsObserver(it) } })
        }
    }
    
    private fun namesObserver(names: Array<String>) {
        tvGameListHeaderNameP1.text = names[0]
        tvGameListHeaderNameP2.text = names[1]
        tvGameListHeaderNameP3.text = names[2]
        tvGameListHeaderNameP4.text = names[3]
    }
    
    private fun totalsObserver(totals: Array<String>) {
        tvGameListFooterTotalPointsP1?.text = totals[0]
        tvGameListFooterTotalPointsP2?.text = totals[1]
        tvGameListFooterTotalPointsP3?.text = totals[2]
        tvGameListFooterTotalPointsP4?.text = totals[3]
    }
}
