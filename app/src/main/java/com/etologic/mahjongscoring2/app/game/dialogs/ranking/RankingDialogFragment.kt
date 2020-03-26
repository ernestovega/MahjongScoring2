package com.etologic.mahjongscoring2.app.game.dialogs.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.lifecycle.Observer
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GamePages.LIST
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.EXIT
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import kotlinx.android.synthetic.main.game_table_ranking_dialog_fragment.*

internal class RankingDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "RankingDialogFragment"
    }
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_table_ranking_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        setOnClickListeners()
        initObservers()
        activityViewModel?.loadRankingData()
    }
    
    private fun initObservers() {
        activityViewModel?.getRankingData()?.observe(viewLifecycleOwner, Observer(this@RankingDialogFragment::fillRankingViews))
    }
    
    private fun setOnClickListeners() {
        btRankingDialogSeeList?.setOnClickListener {
            activityViewModel?.showPage(LIST)
            dismiss()
        }
        btRankingDialogResume?.setOnClickListener {
//            activityViewModel?.resumeGame()
        }
        btRankingDialogClose?.setOnClickListener {
            dismiss()
        }
        btRankingDialogExit?.setOnClickListener {
            activityViewModel?.navigateTo(EXIT)
            dismiss()
        }
    }
    
    private fun fillRankingViews(rankingData: RankingData) {
            val playerFirst = rankingData.sortedPlayersRankings[0]
            val playerSecond = rankingData.sortedPlayersRankings[1]
            val playerThird = rankingData.sortedPlayersRankings[2]
            val playerFourth = rankingData.sortedPlayersRankings[3]

            tvRankingDialogPlayer1Name?.text = playerFirst.name
            tvRankingDialogPlayer2Name?.text = playerSecond.name
            tvRankingDialogPlayer3Name?.text = playerThird.name
            tvRankingDialogPlayer4Name?.text = playerFourth.name
            tvRankingDialogPlayer1Points?.text = playerFirst.points
            tvRankingDialogPlayer2Points?.text = playerSecond.points
            tvRankingDialogPlayer3Points?.text = playerThird.points
            tvRankingDialogPlayer4Points?.text = playerFourth.points
            tvRankingDialogPlayer1Score?.text = playerFirst.score
            tvRankingDialogPlayer2Score?.text = playerSecond.score
            tvRankingDialogPlayer3Score?.text = playerThird.score
            tvRankingDialogPlayer4Score?.text = playerFourth.score
            tvRankingDialogBestHandPlayerPoints?.text = rankingData.bestHandPlayerPoints
            tvRankingDialogNumRounds?.text = rankingData.sNumRounds
//            tvDuration?.setText(rankingTable.getDuration());
            if (rankingData.numRounds < 16) {
                btRankingDialogResume?.visibility = View.VISIBLE
                btRankingDialogResume?.setOnClickListener { /*activityViewModel?.resumeGame()*/ }
            } else {
                btRankingDialogResume?.visibility = View.GONE
            }
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}
