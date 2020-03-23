package com.etologic.mahjongscoring2.app.game.game_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R.layout
import com.etologic.mahjongscoring2.app.game.base.BaseGameFragment
import kotlinx.android.synthetic.main.game_list_fragment.*
import kotlinx.android.synthetic.main.game_list_fragment.view.*
import javax.inject.Inject

class GameListFragment : BaseGameFragment() {
    
    @Inject
    internal lateinit var rvAdapter: GameListRvAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.game_list_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        initViewModel()
    }
    
    private fun setupRecyclerView(view: View) {
        view.rvGameList?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        layoutManager.stackFromEnd = true
        view.rvGameList?.layoutManager = layoutManager
        view.rvGameList?.adapter = rvAdapter
    }
    
    private fun initViewModel() {
        activityViewModel?.getListNames()?.observe(viewLifecycleOwner, Observer(this::namesObserver))
        activityViewModel?.getListRounds()?.observe(viewLifecycleOwner, Observer(rvAdapter::updateCollection))
        activityViewModel?.getListTotals()?.observe(viewLifecycleOwner, Observer(this::totalsObserver))
    }
    
    private fun namesObserver(names: Array<String>) {
        tvGameListHeaderNameP1?.text = names[0]
        tvGameListHeaderNameP2?.text = names[1]
        tvGameListHeaderNameP3?.text = names[2]
        tvGameListHeaderNameP4?.text = names[3]
    }
    
    private fun totalsObserver(totals: Array<String>) {
        tvGameListFooterTotalPointsP1?.text = totals[0]
        tvGameListFooterTotalPointsP2?.text = totals[1]
        tvGameListFooterTotalPointsP3?.text = totals[2]
        tvGameListFooterTotalPointsP4?.text = totals[3]
    }
}