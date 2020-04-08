package com.etologic.mahjongscoring2.app.game.dialogs.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MAX_MCR_ROUNDS
import kotlinx.android.synthetic.main.game_table_ranking_dialog_fragment.*
import java.lang.String.format
import java.util.*
import java.util.Locale.*

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
        setOnClickListeners()
        activityViewModel?.loadRankingData()?.let(this::fillRankingViews)
    }
    
    private fun setOnClickListeners() {
        btRankingDialogResume?.setOnSecureClickListener {
            activityViewModel?.resumeGame()
            dismiss()
        }
        btRankingDialogOk?.setOnSecureClickListener {
            dismiss()
        }
    }
    
    private fun fillRankingViews(rankingData: RankingData) {
        val playerFirst = rankingData.sortedPlayersRankings[0]
        val playerSecond = rankingData.sortedPlayersRankings[1]
        val playerThird = rankingData.sortedPlayersRankings[2]
        val playerFourth = rankingData.sortedPlayersRankings[3]
        
        btRankingDialogResume?.visibility = if (rankingData.numRounds < MAX_MCR_ROUNDS) VISIBLE else GONE
        
        tvRankingDialogPlayer1Name?.text = playerFirst.name
        tvRankingDialogPlayer2Name?.text = playerSecond.name
        tvRankingDialogPlayer3Name?.text = playerThird.name
        tvRankingDialogPlayer4Name?.text = playerFourth.name
        
        tvRankingDialogPlayer1Points?.text = playerFirst.points
        tvRankingDialogPlayer2Points?.text = playerSecond.points
        tvRankingDialogPlayer3Points?.text = playerThird.points
        tvRankingDialogPlayer4Points?.text = playerFourth.points
        
        tvRankingDialogPlayer1Score?.text = format(getDefault(),"%+d", playerFirst.score)
        tvRankingDialogPlayer2Score?.text = format(getDefault(),"%+d", playerSecond.score)
        tvRankingDialogPlayer3Score?.text = format(getDefault(),"%+d", playerThird.score)
        tvRankingDialogPlayer4Score?.text = format(getDefault(),"%+d", playerFourth.score)
        
        tvRankingDialogBestHandPlayerPoints?.text = rankingData.bestHandPlayerPoints
        tvRankingDialogBestHandPlayerName?.text = rankingData.bestHandPlayerName
        
        tvRankingDialogNumRounds?.text = rankingData.sNumRounds
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}
